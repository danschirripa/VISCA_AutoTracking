g++ -shared -fPIC -I"/home/dan/jdk/include" -I"/home/dan/jdk/include/linux" -I"/home/dan/NDI SDK for Linux/include" -L. -l:libndi.so -o libndisender.so NDISender.cpp
