from django.shortcuts import render


def login(request):
	context = {}
	template = "login.html"
	return render(request, template, context)
