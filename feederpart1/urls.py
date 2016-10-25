from django.conf.urls import url
from django.contrib import admin
from feederpart1.views import *

urlpatterns = [

	url(r'^login/', loginform, name='loginform'),
	url(r'^login1234/', login, name='login'),
	url(r'^registerform/', registerform, name='registerform'),
	url(r'^register/', register, name='register'),
	url(r'^adminlogin/', adminlogin, name='adminlogin'),
	url(r'^addcourse/', addcourse, name='addcourse')
]