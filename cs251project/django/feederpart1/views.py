from django.shortcuts import render
from django.contrib.auth import authenticate,login
from django.http import HttpResponse
from django.http import JsonResponse
from .forms import *
from .models import *
from django.contrib.auth.models import User
from django.contrib.auth import logout
import datetime



def loginform(request):
	return render(request, "login.html", {})

def studentlogin(request):
	rollno='none'
	password='none'
	if request.method == 'POST':
		myform=StudentLoginForm(data = request.POST)
		if myform.is_valid():
			myform=myform.cleaned_data
			rollno=myform['rollno']
			password=myform['password']

			if Student.objects.filter(rollno=rollno).exists():
				real_password=Student.objects.get(rollno=rollno).password
				name=Student.objects.get(rollno=rollno).username
				if password == real_password:
					# return render(request, "loggedin.html", {"username":username,"courses":Course.objects.all()})
					return JsonResponse('Welcome '+name.split()[0], safe=False)
				else:
					# return render(request, 'blankMessage.html',{"message":'password don\'t match'})
					return JsonResponse('Password Mismatch', safe=False)
			else:
				# return render(request, 'blankMessage.html',{"message":username+' username not found.'})
				return JsonResponse('Roll no. not Found', safe=False)
		else:
			# return render(request, 'blankMessage.html',{"message":'Error in input'})
			return JsonResponse('Invalid Input', safe=False)
	else:
			# return render(request, 'blankMessage.html',{"message":'Connection problem'})
			return JsonResponse('error.', safe=False)

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
					return render(request, "loggedin.html", {})
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

def userhome(request):
	if request.user.is_authenticated:	
		return render(request, "loggedin.html", {"courses":Course.objects.all()})
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
				if Course.objects.filter(code=code).exists():
					return render(request, 'blankMessage.html',{"message":name+' '+code+' course code all ready exist.'})

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

# def editfeedback(request, coursecode, feedbackname):
# 	if request.user.is_authenticated==False:
# 		return render(request, 'blankMessage.html',{"message":'forbidden.'})
# 	questions=Course.objects.get(code=coursecode).feedbackforms.get(name=feedbackname).questions.all()
# 	return render(request, "editfeedback.html", {"questions":questions})

def addstudents(request, coursecode):
	if request.user.is_authenticated:
		return render(request, "addstudents.html", {"addedstudents":Course.objects.get(code=coursecode).students.all(),
													"students":Student.objects.all(),
													"coursecode":coursecode,
													"user":request.user})
	else:
		return render(request, 'blankMessage.html',{"message":'forbidden.'})

def addstudent(request, coursecode, rollno):
	if request.user.is_authenticated:
		c=Course.objects.get(code=coursecode)
		c.students.add(Student.objects.get(rollno=rollno))
		c.save()
		return render(request, "addstudents.html", {"addedstudents":Course.objects.get(code=coursecode).students.all(),
													"students":Student.objects.all(),
													"coursecode":coursecode,
													"user":request.user})
	else:
		return render(request, 'blankMessage.html',{"message":'forbidden.'})

def deletecourse(request, coursecode):
	if request.user.is_authenticated==False:
		return render(request, 'blankMessage.html',{"message":'forbidden.'})

	Course.objects.filter(code=coursecode).delete()
	return render(request, "addcourses.html", {"courses":Course.objects.all()})

def addquestion(request, coursecode ,feedbackname):
	if request.user.is_authenticated==False:
		return render(request, 'blankMessage.html',{"message":'forbidden.'})
	if request.method == 'POST':
		myform=AddQuestionForm(data = request.POST)
		if myform.is_valid():
			myform=myform.cleaned_data
			q=Question(text=myform['Qtext'])
			q.save()
			c=Course.objects.get(code=coursecode).feedbackforms.filter(name=feedbackname)[0]
			c.questions.add(q)
			c.save()
			return render(request, "editfeedback.html", {"questions":c.questions.all(),"coursecode":coursecode,"feedbackname":feedbackname})
		else:
			return render(request, 'blankMessage.html',{"message":'Error in input'})
	else:
		return render(request, 'blankMessage.html',{"message":'Connection problem.'})

def addfeedback(request):
	if request.user.is_authenticated==False:
		return render(request, 'blankMessage.html',{"message":'forbidden.'})

	coursecode='none'
	feedbackname='none'
	# feedbackdeadline='none'
	if request.method == 'POST':
		myform=AddFeedbackForm(data = request.POST)
		if myform.is_valid():
			myform=myform.cleaned_data
			coursecode=myform['coursecode'] 
			feedbackname=myform['feedbackname']
			# feedbackdeadline=myform['feedbackdeadline']

			f=Feedbackform(
				name=feedbackname,
				# deadline=feedbackdeadline
				)
			f.save()
			c=Course.objects.get(code=coursecode)
			c.feedbackforms.add(f)
			c.save()
			questions=c.feedbackforms.filter(name=feedbackname)[0].questions.all()
			context={"questions":questions,"coursecode":coursecode,"feedbackname":feedbackname}
			return render(request, "editfeedback.html", context)
		else:
			return render(request, 'blankMessage.html',{"message":'Error in input'})
	else:
			return render(request, 'blankMessage.html',{"message":'Connection problem.'})


def addassignment(request):
	if request.user.is_authenticated==False:
		return render(request, 'blankMessage.html',{"message":'forbidden.'})

	coursecode='none'
	assignmentname='none'
	assignmentdeadline='none'
	if request.method == 'POST':
		myform=AddAssignmentForm(data = request.POST)
		if myform.is_valid():
			myform=myform.cleaned_data
			coursecode=myform['coursecode'] 
			assignmentname=myform['assignmentname']
			assignmentdeadline=myform['assignmentdeadline']

			a=Assignment(
				name=assignmentname,
				deadline=assignmentdeadline
				)
			a.save()
			c=Course.objects.get(code=coursecode)
			c.assignments.add(a)
			c.save()
			return render(request, "loggedin.html", context=None)

		else:
			return render(request, 'blankMessage.html',{"message":'Error in input'})
	else:
			return render(request, 'blankMessage.html',{"message":'Connection problem.'})



def getstudentdata(request, rollno):

	s=Student.objects.get(rollno=rollno)
	courses=s.course_set.all()
	l=[]
	for i in range(len(list(courses))):
		f=[]
		a=[]
		c=Course.objects.get(code=courses[i].code).feedbackforms.all()
		for j in range(len(list(c))):
			f.append({"name":c[j].name ,"deadline":c[j].deadline })
		c=Course.objects.get(code=courses[i].code).assignments.all()
		for j in range(len(list(c))):
			a.append({"name":c[j].name ,"deadline":c[j].deadline })
		l.append({"coursecode":courses[i].code,"feedbackforms":f,"assignments":a})


	return JsonResponse({'courses':l})
	
def getquestions(request, coursecode ,feedbackname):

	q=Course.objects.get(code=coursecode).feedbackforms.get(name=feedbackname).questions.all()
	l=[]
	for i in range(len(list(q))):
		l.append({"text":q[i].text})
	return JsonResponse({'questions':l})

def sentfeedback(request):
	rollno='none'
	coursecode='none'
	feedbackname='none'
	feedbackdata='none'
	if request.method == 'POST':
		myform=SentFeedbackForm(data = request.POST)
		if myform.is_valid():
			myform=myform.cleaned_data
			rollno=myform['rollno']
			coursecode=myform['coursecode']
			feedbackname=myform['feedbackname']
			feedbackdata=myform['feedbackdata']

			qs=Course.objects.get(code=coursecode).feedbackforms.get(name=feedbackname).questions.all()
			r1=Response(rating='1')
			r2=Response(rating='2')
			r3=Response(rating='3')
			r4=Response(rating='4')
			r5=Response(rating='5')
			r1.save()
			r2.save()
			r3.save()
			r4.save()
			r5.save()
			r=[r1,r2,r3,r4,r5]

			for i in range(len(list(qs))):
				qs[i].responses.add(r[int(feedbackdata[i])-1])
			return JsonResponse('saved', safe=False)
		else:
			# return render(request, 'blankMessage.html',{"message":'Error in input'})
			return JsonResponse('Invalid Input try again', safe=False)
	else:
			# return render(request, 'blankMessage.html',{"message":'Connection problem'})
			return JsonResponse('error try again', safe=False)

def viewallresponseof(request, coursecode ,feedbackname):
	# if request.user.is_authenticated==False:
	# 	return render(request, 'blankMessage.html',{"message":'forbidden.'})

	feedback=Course.objects.get(code=coursecode).feedbackforms.get(name=feedbackname)
	return render(request, 'viewallresponse.html',{"feedback":feedback})