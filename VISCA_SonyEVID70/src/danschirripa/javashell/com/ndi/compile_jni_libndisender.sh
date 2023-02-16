g++ -shared -fPIC -I"/home/dan/jdk-18.0.1/include" -I"/home/dan/jdk-18.0.1/include/linux" -I"/home/dan/NDI SDK for Linux/include" -L. -l:libndi.so -o libndisender.so NDISender.cpp
