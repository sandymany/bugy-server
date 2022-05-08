import time
import requests
from bs4 import BeautifulSoup as BS
import json

def printInfo ():
	html = requests.get(baseURL+"/info")
	return (BS(html.content,"lxml").text)

def getBugs (toSearch):
	html = requests.get(baseURL+"/getBugs?"+toSearch)
	return(BS(html.content,"lxml").text)

def logIn (username,password):
	html = requests.get(url= baseURL+"/login",params = {"username":username,"password":password})
	return (BS(html.content,"lxml").text)

def register (username,password):
	request = requests.post(baseURL+"/register", data = {"username":username,"password":password})
	print(request.headers)
	return(BS(request.content,"lxml").text)

def searchBugs (sessionCookie,toSearch):
	html = requests.get(baseURL+"/home/searchBugs?",params ={"sessionCookie":sessionCookie,"toSearch":toSearch})
	return(BS(html.content,"lxml").text)

def logOut (sessionCookie):
	response = requests.get(baseURL+"/logOut",params = {"sessionCookie":sessionCookie})
	return(BS(response.content,"lxml").text)

baseURL = "http://localhost:8000"

#print(getBugs("Zygentoma"))

#print(login("novi_user","novi_pass"))
try:
	cookie = logIn("ana","talan")
	print("cookie: ",cookie)
except Exception as e:
	print(e)
#time.sleep(3)
#searchBugs(cookie,"Zygentoma")



#cook3 = logIn("ber","1234")
#print("cook3: ",cook3)

#cook4 = logIn("ivan","112")
#print("cook4: ",cook4)
#logOut(cook4)
#logOut(cook3)

#sessionCookie = login("let","1234")
#login("ber","1234")
#login("ivan","112")

#za login
"""
if html != "false":
	print("welcome!")
	print(html)
	sessionCookie = html
else:
	print("you are not yet registered")
"""

#searchani_kukci = searchBugs("busjndj","Mantodea")
#print(searchani_kukci)
#print()

#print(searchBugs(sessionCookie,"Zygentoma"))


"""
if (html != "true"):
	print("you are registered") # ako nema jos nikoga s tim imenom
	print(html)

else :
	print("Invalid username or password.") #ako ima vec nekog s tim imenom
"""

