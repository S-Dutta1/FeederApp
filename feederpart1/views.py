from django.shortcuts import render
from django.http import HttpResponse
from .forms import *
from .models import Student

def loginform(request):
	context = {}
	template = "login.html"
	return render(request, template, context)

def login(request):

	context = {}
	username='none'
	password='none'
	if request.method == 'POST':
		myform=ContactForm(data = request.POST)
		if myform.is_valid():
			# myform.user_name="xxx"
			myform=myform.cleaned_data
			username=myform['user_name']
			password=myform['user_password']
			# username='abcd'
			if Student.objects.filter(username=username).exists():
				template = "loggedin.html"
				real_password=Student.objects.get(username=username).password
				if password == real_password:
					return render(request, template, {"username":'succecfully -- username'})
				else:
					return HttpResponse('password don\'t match')
			else:
				return render(request, 'loggedin.html',{"username":username})
				# return HttpResponse("<h2>username not found " + str(username) + " ............</h2>")

		else:
			# template = "login.html"
			# return render(request, template, context)
			return HttpResponse('ERROR')
	else:
			# template = "login.html"
			# return render(request, template, context)
			return HttpResponse('ERROR 1')


def register(request):
	context = {}
	template = "register.html"
	return render(request, template, context)
