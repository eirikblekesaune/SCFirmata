SerialPort.devices
a = FirmataDevice('/dev/tty.usbmodem411');
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