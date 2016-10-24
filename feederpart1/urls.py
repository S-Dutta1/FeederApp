from django.conf.urls import url
from django.contrib import admin
from feederpart1.views import *

urlpatterns = [

	url(r'^login/', loginform, name='loginform'),
	url(r'^login1234/', login, name='login'),
	url(r'^register/', register, name='register')
]