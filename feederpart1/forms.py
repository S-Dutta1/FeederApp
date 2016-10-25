from django import forms
from .models import Student

class ContactForm(forms.Form):
    user_name = forms.CharField(max_length=100,required=True)
    user_password = forms.CharField(widget = forms.PasswordInput(),required=True)

class AdminForm(forms.Form):
	admin_name = forms.CharField(max_length=100,required=True)
	admin_password = forms.CharField(widget = forms.PasswordInput(),required=True)

	