import os
import csv

os.environ.setdefault("DJANGO_SETTINGS_MODULE", "feederserver.settings")
import django
django.setup()

from feederpart1.models import *

with open('abc.csv','r') as f:
	reader=csv.reader(f)
	lista = list(reader)
for i in range(len(lista)):
	s=Student(username=lista[i][0],password=lista[i][2],rollno=lista[i][1])
	s.save()