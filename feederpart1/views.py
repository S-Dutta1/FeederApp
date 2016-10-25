from django.shortcuts import render
from django.http import HttpResponse
from .forms import *
from .models import Instructor

def loginform(request):
	return render(request, "login.html", {})

def login(request):
	username='none'
	password='none'
	if request.method == 'POST':
		myform=ContactForm(data = request.POST)
		if myform.is_valid():
			myform=myform.cleaned_data
			username=myform['user_name']
			password=myform['user_password']
			if Instructor.objects.filter(username=username).exists():
				real_password=Instructor.objects.get(username=username).password
				if password == real_password:
					return render(request, "loggedin.html", {"username":username})
				else:
					return render(request, 'blankMessage.html',{"message":'password don\'t match'})
			else:
				return render(request, 'blankMessage.html',{"message":username+' username not found.'})
		else:
			return render(request, 'blankMessage.html',{"message":'Error in input'})
	else:
			return render(request, 'blankMessage.html',{"message":'Connection problem'})

def adminlogin(request):
	admin_username='assassin'
	admin_password='group08'
	if request.method == 'POST':
		myform=AdminForm(data = request.POST)
		if myform.is_valid():
			myform=myform.cleaned_data
			username=myform['admin_name']
			password=myform['admin_password']
			if admin_username==username:
				if admin_password == password:
					return render(request, "addcourses.html", {"courses":Course.objects.all()})
				else:
					return render(request, 'blankMessage.html',{"message":'password don\'t match'})
			else:
				return render(request, 'blankMessage.html',{"message":username+' username not found.'})
		else:
			return render(request, 'blankMessage.html',{"message":'Error in input'})
	else:
			return render(request, 'blankMessage.html',{"message":'Connection problem  .'})



def registerform(request):
	return render(request, "register.html", {})

def register(request):
	username='none'
	fullname='none'
	instiname='none'
	password='none'
	re_password='none'
	email='none'
	if request.method == 'POST':
		myform=RegisterForm(data = request.POST)
		if myform.is_valid():
			myform=myform.cleaned_data
			fullname=myform['fullname'] 
			email=myform['email']
			username=myform['username']
			password=myform['password']
			re_password=myform['re_password']
			instiname=myform['instiname']

			if Instructor.objects.filter(username=username).exists():
				return render(request, 'blankMessage.html',{"message":username+' username all ready exist.'})
			if password != re_password:
				return render(request, 'blankMessage.html',{"message":'password dont match.'})

			bc=Instructor(
				username=username,
				fullname=fullname,
				instiname=instiname,
				password=password,
				email=email
				)
			bc.save()
			return render(request, 'blankMessage.html',{"message":username+' registered successfully.'})

		else:
			return render(request, 'blankMessage.html',{"message":'Error in input'})
	else:
			return render(request, 'blankMessage.html',{"message":'Connection problem.'})

