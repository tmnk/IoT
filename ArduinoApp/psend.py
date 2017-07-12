import serial
import time


connected = False
ser = serial.Serial("COM7", 9600)

while not connected:
    serin = ser.read()
    connected = True

#comands here
ser.write(b"1")
time.sleep(2)
ser.write(b"3")
ser.write(b"5")
time.sleep(2)
# ser.write(b"10")
ser.close()
