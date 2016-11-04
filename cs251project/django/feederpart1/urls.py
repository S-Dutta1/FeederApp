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
	url(r'^userhome/', userhome, name='userhome'),
	url(r'^addcourse/', addcourse, name='addcourse'),

	url(r'^addassignment/', csrf_exempt(addassignment), name='addassignment'),
	url(r'^addquestion/(\w+)/(\w+)/', csrf_exempt(addquestion), name='addquestion'),

	url(r'^deletecourse/(\w+)/', deletecourse, name='deletecourse'),
	url(r'^addstudents/(\w+)/', addstudents, name='addstudents'),
	url(r'^addstudent/(\w+)/(\w+)/', addstudent, name='addstudent'),

	url(r'^addfeedback/', csrf_exempt(addfeedback), name='addfeedback'),
	# url(r'^feedbackof/(\w+)/(\w+)/', editfeedback, name = 'editfeedback'),
	url(r'^viewallresponseof/(\w+)/(\w+)/', viewallresponseof, name = 'viewallresponseof'),

	url(r'^studentlogin/', csrf_exempt(studentlogin), name='studentlogin'),
	url(r'^sentfeedback/', csrf_exempt(sentfeedback), name='sentfeedback'),
	url(r'^getstudentdata/(\w+)/', csrf_exempt(getstudentdata), name='getstudentdataerfgb'),
	url(r'^getquestions/(\w+)/(\w+)/', csrf_exempt(getquestions), name='getquestions')
]