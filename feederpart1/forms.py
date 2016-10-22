from django import forms
from .models import Post


class ContactForm(forms.Form):
    user_name = forms.CharField(,max_length=100,required=False)
    user_password = forms.CharField(max_length=100,required=False)
