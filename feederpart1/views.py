from django.shortcuts import render
from django.http import HttpResponse
from .forms import *
from .models import Student

def loginform(request):
	context = {}
	template = "login.html"
	return render(request, template, context)

def login(request):
	username='none'
	password='none'
	if request.method == 'POST':
		myform=ContactForm(data = request.POST)
		if myform.is_valid():
			myform=myform.cleaned_data
			username=myform['user_name']
			password=myform['user_password']
			if Student.objects.filter(username=username).exists():
				real_password=Student.objects.get(username=username).password
				if password == real_password:
					return render(request, "loggedin.html", {"username":username})
				else:
					return HttpResponse('password don\'t match')
			else:
				return render(request, 'blankMessage.html',{"message":username+' username not found.'})
		else:
			return render(request, 'blankMessage.html',{"message":'Error in input'})
	else:
			return render(request, 'blankMessage.html',{"message":'Connection problem'})


def register(request):
	context = {}
	template = "register.html"
	return render(request, template, context)
