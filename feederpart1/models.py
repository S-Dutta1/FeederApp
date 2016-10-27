from django.db import models

# Create your models here.
class Instructor(models.Model):
	username=models.CharField(max_length=50, unique=True)
	fullname=models.CharField(max_length=50)
	instiname=models.CharField(max_length=50)
	password=models.CharField(max_length=20)
	email=models.CharField(max_length=50)
	def __str__(self):
		return self.username

class Student(models.Model):
	username=models.CharField(max_length=50, unique= True)
	password=models.CharField(max_length=20)
	def __str__(self):
		return self.username

class Course(models.Model):
	name=models.CharField(max_length=50)
	code=models.CharField(max_length=10)
	students=models.ManyToManyField(Student, blank=True)
	def __str__(self):
		return self.name+' '+self.code

class Response(models.Model):
	rating=models.IntegerField(default=0)
	# timestamp
	# student=models.OneToOne(Student) # so that one student can give feedback only once

class Quesion(models.Model):
	text=models.CharField(max_length=500)
	responses=models.ManyToManyField(Response,blank=True)

class Feedbackform(models.Model):
	name=models.CharField(max_length=50)
	quesions=models.ManyToManyField(Quesion,blank=True)

	##############################################################
	##########################   HOW TO ADD FEEDBACK 	##########
	##############################################################
	# f=feedbak(name='form1')
	# f.save()
	# q1=Quesion(text='is sharat chutiya?')
	# q1.save()
	# q2 ...
	# q2 ...
	# f.quesions.add(q1,q2,...)

	# # for all students
	# r=Response(rating=3)
	# r.save()
	# feedbak.quesions.all()[0].responses.add(r)
	# len(list(feedbak.quesions.all()[0].responses.filter(rating = 5))) //will give no of responses with 5