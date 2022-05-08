import json
import requests
from bs4 import BeautifulSoup as BS

def dumpjson(f):
    with open('/home/leticija/projects/bugy/resources/insectdatabase.json','w') as outfile:
        outfile.write(json.dumps(f,indent=4))

mainjson = {"database":[]}


def recursion(taxon,row_dict):
    global mainjson
    subnodes = taxon.find_all(["span","div"],recursive=False)
    formatted = []
    while subnodes:
        if(len(subnodes) <= 3):
            formatted.append(subnodes)
            break
        formatted.append(subnodes[:3] if "div" in subnodes[2].name else subnodes[:2])
        subnodes = subnodes[3:] if len(formatted[-1]) == 3 else subnodes[2:]
    for node in formatted:
        row_dict[node[0].text.strip().lower()] = node[1].find("b").text.strip()
        if "species" in node[0].text.lower():
            if node[1].find(text=True,recursive=False):
                row_dict["binomial name"] = node[1].find(text=True,recursive=False).replace("-","").strip()
            row_dict["url"] = node[1].find("a")["href"]
            print(row_dict)
            mainjson["database"].append(row_dict)
        if(len(node) == 3):
            recursion(node[2],row_dict.copy())

with open('/home/leticija/projects/bugy/resources/bugguide.html','r') as infile:
    soup = BS(infile.read(),"lxml")
    soup = soup.find("div",{"class":"bgpage-taxon"})
    recursion(soup,{})
    dumpjson(mainjson)
