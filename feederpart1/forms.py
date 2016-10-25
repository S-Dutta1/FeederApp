from django import forms
from .models import Student

class ContactForm(forms.Form):
    user_name = forms.CharField(max_length=50,required=True)
    user_password = forms.CharField(widget = forms.PasswordInput(),required=True)

class RegisterForm(forms.Form):
	fullname = forms.CharField(max_length=50,required=True)
	email = forms.CharField(max_length=50,required=True)
	username = forms.CharField(max_length=50,required=True)
	password = forms.CharField(widget = forms.PasswordInput(),required=True)
	re_password = forms.CharField(widget = forms.PasswordInput(),required=True)
	instiname = forms.CharField(max_length=50,required=True)

class AdminForm(forms.Form):
	admin_name = forms.CharField(max_length=50,required=True)
	admin_password = forms.CharField(widget = forms.PasswordInput(),required=True)

class AddCourseForm(forms.Form):
	name = forms.CharField(max_length=50,required=True)
	code = forms.CharField(max_length=10,required=True)

	