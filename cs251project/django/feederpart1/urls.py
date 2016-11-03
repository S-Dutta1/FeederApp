from django.conf.urls import url
from django.contrib import admin
from feederpart1.views import *
from django.views.decorators.csrf import csrf_exempt

urlpatterns = [

	url(r'^login/', loginform, name='loginform'),
	url(r'^logout/',Logout, name='logout'),
	url(r'^registerform/', registerform, name='registerform'),
	url(r'^register/', register, name='register'),
	url(r'^adminlogin/', adminlogin, name='adminlogin'),
	url(r'^adminhome/', adminhome, name='adminhome'),
	url(r'^addcourse/', addcourse, name='addcourse'),
	url(r'^addassignment/', addassignment, name='addassignment'),
	url(r'^addquestion/(\w+)/(\w+)/', addquestion, name='addquestion'),
	url(r'^deletecourse/(\w+)/', deletecourse, name='deletecourse'),
	url(r'^addstudents/(\w+)/', addstudents, name='addstudents'),
	url(r'^addstudent/(\w+)/(\w+)/', addstudent, name='addstudent'),
	url(r'^addfeedback/', addfeedback, name='addfeedback'),
	url(r'^feedbackof/(\w+)/(\w+)/', editfeedback, name = 'editfeedback'),

	url(r'^studentlogin/', csrf_exempt(studentlogin), name='studentlogin'),
	url(r'^coursenames/(\w+)/', csrf_exempt(coursenames), name='coursenames')
]