# -*- coding: utf-8 -*-
# Generated by Django 1.10.2 on 2016-10-30 13:36
from __future__ import unicode_literals

import datetime
from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('feederpart1', '0011_auto_20161029_0309'),
    ]

    operations = [
        migrations.CreateModel(
            name='Assignment',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=50)),
                ('urllink', models.CharField(blank=True, max_length=200)),
                ('deadline', models.DateTimeField(blank=True, default=datetime.datetime.now)),
            ],
        ),
        migrations.AddField(
            model_name='feedbackform',
            name='deadline',
            field=models.DateTimeField(blank=True, default=datetime.datetime.now),
        ),
        migrations.AddField(
            model_name='course',
            name='assignments',
            field=models.ManyToManyField(blank=True, to='feederpart1.Assignment'),
        ),
    ]
