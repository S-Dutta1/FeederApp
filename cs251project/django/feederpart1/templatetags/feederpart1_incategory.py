from django import template
from django.template.defaultfilters import stringfilter
register = template.Library()

@register.filter
@stringfilter
def incategory(things, category):
    return things.filter(rating=category)
    