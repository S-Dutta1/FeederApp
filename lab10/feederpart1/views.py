from django.shortcuts import render
from django.contrib.auth import authenticate,login
from django.contrib.auth.decorators import login_required
from django.http import HttpResponse
from .forms import *
from .models import *
from django.contrib.auth.models import User
from django.contrib.auth import logout
import datetime



def loginform(request):
	return render(request, "login.html", {})

def Login(request):
	username='none'
	password='none'
	if request.method == 'POST':
		myform=ContactForm(data = request.POST)
		if myform.is_valid():
			myform=myform.cleaned_data
			username=myform['user_name']
			password=myform['user_password']

			user = authenticate(username=username, password=password)

			if user is not None:
			
				login(request, user)
				return render(request, "loggedin.html", {"username":username,"courses":Course.objects.all()})
			else:
			    # No backend authenticated the credentials
				return render(request, 'blankMessage.html',{"message":'Authentication Fail'})

			# if Instructor.objects.filter(username=username).exists():
			# 	real_password=Instructor.objects.get(username=username).password
			# 	if password == real_password:
			# 		return render(request, "loggedin.html", {"username":username,"courses":Course.objects.all()})
			# 	else:
			# 		return render(request, 'blankMessage.html',{"message":'password don\'t match'})
			# else:
			# 	return render(request, 'blankMessage.html',{"message":username+' username not found.'})
		else:
			return render(request, 'blankMessage.html',{"message":'Error in input'})
	else:
			return render(request, 'blankMessage.html',{"message":'Connection problem'})

	# request.user.is_authenticated
	# u=request.user

def Logout(request):
	logout(request)
	return render(request, "login.html", {})

def adminlogin(request):
	# admin_username='assassin'
	# admin_password='group08'
	if request.method == 'POST':
		myform=AdminForm(data = request.POST)
		if myform.is_valid():
			myform=myform.cleaned_data
			username=myform['admin_name']
			password=myform['admin_password']

			user = authenticate(username=username, password=password)


			if user is not None:
			    # A backend authenticated the credentials
				if user.is_superuser:
					login(request, user)
					return render(request, "addcourses.html", {"courses":Course.objects.all()})
				else:
					login(request, user)
					return render(request, "loggedin.html", {"username":username,"courses":Course.objects.all()})
			else:
				# No backend authenticated the credentials
				return render(request, 'blankMessage.html',{"message":'Authentication Fail'})

			# if admin_username==username:
			# 	if admin_password == password:
			# 		return render(request, "addcourses.html", {"courses":Course.objects.all()})
			# 	else:
			# 		return render(request, 'blankMessage.html',{"message":'password don\'t match'})
			# else:
			# 	return render(request, 'blankMessage.html',{"message":username+' username not found.'})
		else:
			return render(request, 'blankMessage.html',{"message":'Error in input'})
	else:
			return render(request, 'blankMessage.html',{"message":'Connection problem  .'})

def adminhome(request):
	if request.user.is_authenticated:	
		return render(request, "addcourses.html", {"courses":Course.objects.all()})
	else:
		return render(request, 'blankMessage.html',{"message":'forbidden.'})


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
			username=myform['username'] 
			email=myform['email']
			password=myform['password']
			re_password=myform['re_password']

			if password != re_password:
				return render(request, 'blankMessage.html',{"message":'password dont match.'})

			# bc=Instructor(
			# 	username=username,
			# 	fullname=fullname,
			# 	instiname=instiname,
			# 	password=password,
			# 	email=email
			# 	)
			# bc.save()
			user = User.objects.create_user(username=username,
											email=email,
											password=password)
			user.save()
			return render(request, "loggedin.html", {"username":username,"courses":Course.objects.all()})

		else:
			return render(request, 'blankMessage.html',{"message":'Error in input'})
	else:
			return render(request, 'blankMessage.html',{"message":'Connection problem.'})


def addcourse(request):

	if request.user.is_authenticated:
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
				presentdate = datetime.date.today()
				midsemdate = presentdate.replace(month=9,day=20)
				endsemdate = presentdate.replace(month=11,day=10)
				a1=Assignment(name='midsem',deadline=datetime.datetime.combine(midsemdate,datetime.time(12, 0, 0)))
				a1.save()
				a2=Assignment(name='endsem',deadline=datetime.datetime.combine(endsemdate,datetime.time(12, 0, 0)))
				a2.save()

				c=Course(
					name=name,
					code=code,
					)
				c.save()
				c.feedbackforms.add(f1,f2)
				c.assignments.add(a1,a2)
				c.save()
				return render(request, "addcourses.html", {"courses":Course.objects.all()})
			else:
				return render(request, 'blankMessage.html',{"message":'Error in input'})
		else:
				return render(request, 'blankMessage.html',{"message":'Connection problem.'})
	else:
		return render(request, 'blankMessage.html',{"message":'forbidden.'})

def editfeedback(request, coursename, coursecode, feedbackname):
	questions=Course.objects.get(name=coursename,code=coursecode).feedbackforms.get(name=feedbackname).questions.all()
	return render(request, "editfeedback.html", {"questions":questions})

def addstudents(request, coursename, coursecode):
	if request.user.is_authenticated:
		return render(request, "addstudents.html", {"addedstudents":Course.objects.get(name=coursename,code=coursecode).students.all(),
													"students":Student.objects.all(),
													"coursename":coursename,
													"coursecode":coursecode,
													"user":request.user})
	else:
		return render(request, 'blankMessage.html',{"message":'forbidden.'})

def addstudent(request, coursename, coursecode, rollno):
	if request.user.is_authenticated:
		c=Course.objects.get(name=coursename,code=coursecode)
		c.students.add(Student.objects.get(rollno=rollno))
		c.save()
		return render(request, "addstudents.html", {"addedstudents":Course.objects.get(name=coursename,code=coursecode).students.all(),
													"students":Student.objects.all(),
													"coursename":coursename,
													"coursecode":coursecode,
													"user":request.user})
	else:
		return render(request, 'blankMessage.html',{"message":'forbidden.'})

def deletecourse(request, coursename, coursecode):
	Course.objects.filter(name=coursename, code=coursecode).delete()
	return render(request, "addcourses.html", {"courses":Course.objects.all()})