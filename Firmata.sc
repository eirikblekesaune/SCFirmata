Firmata {
	classvar
	<analogIOMessage = 0xE0, 
		<digitalIOMessage = 0x90, 
		<reportAnalogPin = 0xC0,
		<reportDigitalPin = 0xD0,
		<sysexStart = 0xF0,
		<setPinMode = 0xF4,
		<sysexEnd = 0xF7,
		<protocolVersion = 0xF9,
		<systemReset = 0xFF,
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
		<pinMode;
		*initClass{
			pinMode = (
				INPUT: 0,
				OUTPUT: 1,
				ANALOG: 2,
				PWM: 3,
				SERVO: 4
			);
		}
}
