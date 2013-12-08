SerialPort.devices
a = FirmataDevice('/dev/tty.usbmodem411');

//Print protoclol version and firmware (i.e. sketch in Arduino terms) is running on the device
a.requestFirmware;
a.requestProtocolVersion;
a.doSystemReset;

//Test with firmware: StandardFirmata.ino
a.setPinMode(13, \OUTPUT);

//set pin 13
a.setDigitalPortMask(1, 2r0100000);//on
a.setDigitalPortMask(1, 2r0000000);//off

//start listening to analog in
a.reportAnalogPin(0, true);
a.analogPinAction_({arg num, val;
	[num, val].postln;
})
a.reportAnalogPin(0, false);

//start listening to a digital port
a.setPinMode(11, \INPUT);
a.reportDigitalPort(1, true);
a.digitalPortAction_({arg portNum, mask;
	"Digital port % : %".format(portNum, mask.asBinaryString).postln;
})

//Updates info about the number of pins available on this device
a.queryCapability;
//Device pin capability data should ow shows how many port and what kind of
//function they can have, and the resolution.
//This data won't be available if the client sketch haven't implemented responses
//capability queries, i.e. this is a part of the protocol, not always the implmetations.
a.pinCapabilities;
a.numberOfPins;


//Test using firmware: EchoString.ino
a.sendRawString("Kjempebra!");//Replies with the same string