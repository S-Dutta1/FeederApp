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
	# class Meta:
	# 	db_table=u'LoginData'
	
	# def __unicode__(self):
	# 	return u"%s %s"%(self.username,self.password)
class Student(models.Model):
	username=models.CharField(max_length=50, unique= True)
	password=models.CharField(max_length=20)
	def __str__(self):
		return self.username

class Course(models.Model):
	name=models.CharField(max_length=50)
	code=models.CharField(max_length=10)
	students =models.ManyToManyField(Student, blank=True)
	def __str__(self):
		return self.name+' '+self.code

