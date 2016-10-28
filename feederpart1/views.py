from django.shortcuts import render
from django.http import HttpResponse
from .forms import *
from .models import *

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


def addcourse(request):
	name='none'
	code='none'
	if request.method == 'POST':
		myform=AddCourseForm(data = request.POST)
		if myform.is_valid():
			myform=myform.cleaned_data
			name=myform['name'] 
			code=myform['code']
			if Course.objects.filter(name=name ,code=code).exists():
				return render(request, 'blankMessage.html',{"message":name+' '+code+' coursename all ready exist.'})

			f1=Feedbackform(name='midsem')
			f1.save()
			q1=Question(text='How did you find the course till now?')
			q1.save()
			q2=Question(text='Rate the difficulty of the course till now?')
			q2.save()
			q3=Question(text='Rate the difficulty of midsem?')
			q3.save()
			f1.questions.add(q1,q2,q3)
			f1.save()

			f2=Feedbackform(name='endsem')
			f2.save()
			q1=Question(text='How did you find the course overall?')
			q1.save()
			q2=Question(text='Rate the difficulty of the course overall?')
			q2.save()
			q3=Question(text='Rate the difficulty of endsem?')
			q3.save()
			f2.questions.add(q1,q2,q3)
			f2.save()

			c=Course(
				name=name,
				code=code,
				)
			c.save()
			c.feedbackforms.add(f1,f2)
			c.save()
			return render(request, "addcourses.html", {"courses":Course.objects.all()})
		else:
			return render(request, 'blankMessage.html',{"message":'Error in input'})
	else:
			return render(request, 'blankMessage.html',{"message":'Connection problem.'})

def editfeedback(request, coursename, coursecode, feedbackname):
	questions=Course.objects.get(name=coursename,code=coursecode).feedbackforms.get(name=feedbackname).questions.all()
	return render(request, "editfeedback.html", {"questions":questions})

def addstudents(request):
	return render(request, "addstudents.html", {"students":Student.objects.all()})
	
