import os
print(":".join(["lib/"+x for x in os.listdir("lib")]+["src","bin"]))