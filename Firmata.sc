FirmataParser {
	classvar
	<analogIOMessage = 0xE0,
	<digitalIOMessage = 0x90,
	<reportAnalogPin = 0xC0,
	<reportDigitalPin = 0xD0,
	<setPinMode = 0xF4,
	<protocolVersion = 0xF9,
	<systemReset = 0xFF,

	//sysex commands
	<sysexStart = 0xF0,
	<sysexEnd = 0xF7,
	<reservedCommand = 0x00, // 2nd SysEx data byte is a chip-specific command (AVR, PIC, TI, etc).
	<analogMappingQuery = 0x69, // ask for mapping of analog to pin numbers
	<analogMappingResponse = 0x6A, // reply with mapping info
	<capabilityQuery = 0x6B, // ask for supported modes and resolution of all pins
	<capabilityResponse = 0x6C, // reply with supported modes and resolution
	<pinStateQuery = 0x6D, // ask for a pin's current mode and value
	<pinStateResponse = 0x6E, // reply with a pin's current mode and value
	<extendedAnalog = 0x6F, // analog write (PWM, Servo, etc) to any pin
	<servoConfig = 0x70, // set max angle, minPulse, maxPulse, freq
	<stringData = 0x71, // a string message with 14-bits per char
	<shiftData = 0x75, // shiftOut config/data message (34 bits)
	<i2cRequest = 0x76, // I2C request messages from a host to an I/O board
	<i2cReply = 0x77, // I2C reply messages from an I/O board to a host
	<i2cConfig = 0x78, // Configure special I2C settings such as power pins and delay times
	<reportFirmware = 0x79, // report name and version of the firmware
	<samplingInterval = 0x7A, // sampling interval
	<sysexNonRealtime = 0x7E, // MIDI Reserved for non-realtime messages
	<sysexRealtime = 0x7F, // MIDI Reserved for realtime messages
	<parserError = -1,
	<pinMode;
	var parseFunctions;
	var state;
	var device;
	var commandData, sysexData;
	var numcommandDataReceived = 0;

	*initClass{
		pinMode = (
			INPUT: 0,
			OUTPUT: 1,
			ANALOG: 2,
			PWM: 3,
			SERVO: 4
		);
	}

	*new{arg device;
		^super.new.init(device);
	}

	init{arg device_;
		device = device_;
		sysexData = Int8Array.new;//sysex can be any size
		commandData = Int8Array.new(32);//max number of data bytes for Firmata is 32 for non-sysex commands
		state = \waitingForCommand;
		parseFunctions = (
			waitingForCommand: {arg byte;
				"got command:%\n".postf(byte.asHexString(2));
				case
				{byte == this.class.protocolVersion} { state = \waitingForProtocolVersionData; }
				{byte == this.class.sysexStart} { state = \waitingForSysexData; };
			},
			waitingForSysexData: {arg byte;
				if(byte != this.class.sysexEnd,
					{ sysexData = sysexData.add(byte);},
					{
						this.parseSysexCommand;
						sysexData = Int8Array.new;
						state = \waitingForCommand;
					}
				);
			},
			\waitingForProtocolVersionData: {arg byte;
				switch(commandData.size,
					0, { commandData.add(byte); },
					1, { "Protocol version: %.%".format(commandData[0], byte).postln; this.reset; }
				);
			}
		);
	}

	reset{
		commandData = Int8Array.new(32);//max number of data bytes for Firmata is 32 for non-sysex commands
		state = \waitingForCommand;
	}

	parseByte{arg byte;
		var nextState;
		parseFunctions.at(state).value(byte);
	}

	//data bytes are sent in two 7bit bytes for sysex, analog,
	parse14BitData{arg data;
		var result;
		data.pairsDo({arg lsb, msb;
			result = result.add(lsb.bitAnd(127).bitOr(msb << 7));
		});
		^result;
	}

	parseSysexCommand{
		switch(sysexData[0],
			this.class.reportFirmware, {
				var version, name;
				version = sysexData.at([1,2]);
				name = String.newFrom(this.parse14BitData(sysexData.copyRange(3, sysexData.size)).collect(_.asAscii));
				"Firmata protocol version: %.% Firmware: %".format(version[0], version[1], name).postln;
			}
		);
	}
}

FirmataDevice {
	var <port; //temp getter
	var parser;
	var listenRoutine;
	var parserState;

	*new{arg portPath, baudrate = 57600;
		^super.new.init(portPath, baudrate);
	}
	init{arg portPath_, baudrate_;
		parser = FirmataParser.new;
		fork{
			port = SerialPort(portPath_, baudrate_, crtscts: true);
			//Wait for Arduino auto reset
			2.wait;
			this.start;
		}
	}

	start{
		listenRoutine = fork {
			loop {
				parser.parseByte(port.read);
			}
		};
	}

	end{ listenRoutine.stop; }
	close{ port.close; }
}
