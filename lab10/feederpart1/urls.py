from django.conf.urls import url
from django.contrib import admin
from feederpart1.views import *

urlpatterns = [

	url(r'^login/', loginform, name='loginform'),
	url(r'^logout/',Logout, name='logout'),
	url(r'^login1234/', Login, name='login'),
	url(r'^registerform/', registerform, name='registerform'),
	url(r'^register/', register, name='register'),
	url(r'^adminlogin/', adminlogin, name='adminlogin'),
	url(r'^addcourse/', addcourse, name='addcourse'),
	url(r'^deletecourse/(\w+)/(\w+)/', deletecourse, name='deletecourse'),
	url(r'^addstudents/(\w+)/(\w+)/', addstudents, name='addstudents'),
	url(r'^addstudent/(\w+)/(\w+)/(\w+)/', addstudent, name='addstudent'),
	url(r'^feedbackof/(\w+)/(\w+)/(\w+)', editfeedback, name = 'editfeedback')
]