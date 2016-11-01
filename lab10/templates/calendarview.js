//
// CalendarView (for Prototype)
// calendarview.org
//
// Maintained by Justin Mecham <justin@aspect.net>
//
// Portions Copyright 2002-2005 Mihai Bazon
//
// This calendar is based very loosely on the Dynarch Calendar in that it was
// used as a base, but completely gutted and more or less rewritten in place
// to use the Prototype JavaScript library.
//
// As such, CalendarView is licensed under the terms of the GNU Lesser General
// Public License (LGPL). More information on the Dynarch Calendar can be
// found at:
//
//   www.dynarch.com/projects/calendar
//

var Calendar = Class.create()

//------------------------------------------------------------------------------
// Constants
//------------------------------------------------------------------------------

Calendar.VERSION = '1.2'

Calendar.DAY_NAMES = new Array(
  'Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday',
  'Sunday'
)

Calendar.SHORT_DAY_NAMES = new Array(
  'S', 'M', 'T', 'W', 'T', 'F', 'S', 'S'
)

Calendar.MONTH_NAMES = new Array(
  'January', 'February', 'March', 'April', 'May', 'June', 'July', 'August',
  'September', 'October', 'November', 'December'
)

Calendar.SHORT_MONTH_NAMES = new Array(
  'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov',
  'Dec' 
)

Calendar.NAV_PREVIOUS_YEAR  = -2
Calendar.NAV_PREVIOUS_MONTH = -1
Calendar.NAV_TODAY          =  0
Calendar.NAV_NEXT_MONTH     =  1
Calendar.NAV_NEXT_YEAR      =  2

//------------------------------------------------------------------------------
// Static Methods
//------------------------------------------------------------------------------

// This gets called when the user presses a mouse button anywhere in the
// document, if the calendar is shown. If the click was outside the open
// calendar this function closes it.
Calendar._checkCalendar = function(event) {
  if (!window._popupCalendar)
    return false
  if (Element.descendantOf(Event.element(event), window._popupCalendar.container))
    return
  window._popupCalendar.callCloseHandler()
  return Event.stop(event)
}

//------------------------------------------------------------------------------
// Event Handlers
//------------------------------------------------------------------------------

Calendar.handleMouseDownEvent = function(event)
{
  Event.observe(document, 'mouseup', Calendar.handleMouseUpEvent)
  Event.stop(event)
}

// XXX I am not happy with how clicks of different actions are handled. Need to
// clean this up!
Calendar.handleMouseUpEvent = function(event)
{
  var el        = Event.element(event)
  var calendar  = el.calendar
  var isNewDate = false

  // If the element that was clicked on does not have an associated Calendar
  // object, return as we have nothing to do.
  if (!calendar) return false

  // Clicked on a day
  if (typeof el.navAction == 'undefined')
  {
    if (calendar.currentDateElement) {
      Element.removeClassName(calendar.currentDateElement, 'selected')
      Element.addClassName(el, 'selected')
      calendar.shouldClose = (calendar.currentDateElement == el)
      if (!calendar.shouldClose) calendar.currentDateElement = el
    }
    calendar.date.setDateOnly(el.date)
    isNewDate = true
    calendar.shouldClose = !el.hasClassName('otherDay')
    var isOtherMonth     = !calendar.shouldClose
    if (isOtherMonth) calendar.update(calendar.date)
  }

  // Clicked on an action button
  else
  {
    var date = new Date(calendar.date)

    if (el.navAction == Calendar.NAV_TODAY)
      date.setDateOnly(new Date())

    var year = date.getFullYear()
    var mon = date.getMonth()
    function setMonth(m) {
      var day = date.getDate()
      var max = date.getMonthDays(m)
      if (day > max) date.setDate(max)
      date.setMonth(m)
    }
    switch (el.navAction) {

      // Previous Year
      case Calendar.NAV_PREVIOUS_YEAR:
        if (year > calendar.minYear)
          date.setFullYear(year - 1)
        break

      // Previous Month
      case Calendar.NAV_PREVIOUS_MONTH:
        if (mon > 0) {
          setMonth(mon - 1)
        }
        else if (year-- > calendar.minYear) {
          date.setFullYear(year)
          setMonth(11)
        }
        break

      // Today
      case Calendar.NAV_TODAY:
        break

      // Next Month
      case Calendar.NAV_NEXT_MONTH:
        if (mon < 11) {
          setMonth(mon + 1)
        }
        else if (year < calendar.maxYear) {
          date.setFullYear(year + 1)
          setMonth(0)
        }
        break

      // Next Year
      case Calendar.NAV_NEXT_YEAR:
        if (year < calendar.maxYear)
          date.setFullYear(year + 1)
        break

    }

    if (!date.equalsTo(calendar.date)) {
      calendar.setDate(date)
      isNewDate = true
    } else if (el.navAction == 0) {
      isNewDate = (calendar.shouldClose = true)
    }
  }

  if (isNewDate) event && calendar.callSelectHandler()
  if (calendar.shouldClose) event && calendar.callCloseHandler()

  Event.stopObserving(document, 'mouseup', Calendar.handleMouseUpEvent)

  return Event.stop(event)
}

Calendar.defaultSelectHandler = function(calendar)
{
  if (!calendar.dateField) return false

  // Update dateField value
  if (calendar.dateField.tagName == 'DIV')
    Element.update(calendar.dateField, calendar.date.print(calendar.dateFormat))
  else if (calendar.dateField.tagName == 'INPUT') {
    calendar.dateField.value = calendar.date.print(calendar.dateFormat) }

  // Trigger the onchange callback on the dateField, if one has been defined
  if (typeof calendar.dateField.onchange == 'function')
    calendar.dateField.onchange()

  // Call the close handler, if necessary
  if (calendar.shouldClose) calendar.callCloseHandler()
}

Calendar.defaultCloseHandler = function(calendar)
{
  calendar.hide()
}


//------------------------------------------------------------------------------
// Calendar Setup
//------------------------------------------------------------------------------

Calendar.setup = function(params)
{

  function param_default(name, def) {
    if (!params[name]) params[name] = def
  }

  param_default('dateField', null)
  param_default('triggerElement', null)
  param_default('parentElement', null)
  param_default('selectHandler',  null)
  param_default('closeHandler', null)

  // In-Page Calendar
  if (params.parentElement)
  {
    var calendar = new Calendar(params.parentElement)
    calendar.setSelectHandler(params.selectHandler || Calendar.defaultSelectHandler)
    if (params.dateFormat)
      calendar.setDateFormat(params.dateFormat)
    if (params.dateField) {
      calendar.setDateField(params.dateField)
      calendar.parseDate(calendar.dateField.innerHTML || calendar.dateField.value)
    }
    calendar.show()
    return calendar
  }

  // Popup Calendars
  //
  // XXX There is significant optimization to be had here by creating the
  // calendar and storing it on the page, but then you will have issues with
  // multiple calendars on the same page.
  else
  {
    var triggerElement = $(params.triggerElement || params.dateField)
    triggerElement.onclick = function() {
      var calendar = new Calendar()
      calendar.setSelectHandler(params.selectHandler || Calendar.defaultSelectHandler)
      calendar.setCloseHandler(params.closeHandler || Calendar.defaultCloseHandler)
      if (params.dateFormat)
        calendar.setDateFormat(params.dateFormat)
      if (params.dateField) {
        calendar.setDateField(params.dateField)
        calendar.parseDate(calendar.dateField.innerHTML || calendar.dateField.value)
      }
      if (params.dateField)
        Date.parseDate(calendar.dateField.value || calendar.dateField.innerHTML, calendar.dateFormat)
      calendar.showAtElement(triggerElement)
      return calendar
    }
  }

}



//------------------------------------------------------------------------------
// Calendar Instance
//------------------------------------------------------------------------------

Calendar.prototype = {

  // The HTML Container Element
  container: null,

  // Callbacks
  selectHandler: null,
  closeHandler: null,

  // Configuration
  minYear: 1900,
  maxYear: 2100,
  dateFormat: '%Y-%m-%d',

  // Dates
  date: new Date(),
  currentDateElement: null,

  // Status
  shouldClose: false,
  isPopup: true,

  dateField: null,


  //----------------------------------------------------------------------------
  // Initialize
  //----------------------------------------------------------------------------

  initialize: function(parent)
  {
    if (parent)
      this.create($(parent))
    else
      this.create()
  },



  //----------------------------------------------------------------------------
  // Update / (Re)initialize Calendar
  //----------------------------------------------------------------------------

  update: function(date)
  {
    var calendar   = this
    var today      = new Date()
    var thisYear   = today.getFullYear()
    var thisMonth  = today.getMonth()
    var thisDay    = today.getDate()
    var month      = date.getMonth();
    var dayOfMonth = date.getDate();

    // Ensure date is within the defined range
    if (date.getFullYear() < this.minYear)
      date.setFullYear(this.minYear)
    else if (date.getFullYear() > this.maxYear)
      date.setFullYear(this.maxYear)

    this.date = new Date(date)

    // Calculate the first day to display (including the previous month)
    date.setDate(1)
    date.setDate(-(date.getDay()) + 1)

    // Fill in the days of the month
    Element.getElementsBySelector(this.container, 'tbody tr').each(
      function(row, i) {
        var rowHasDays = false
        row.immediateDescendants().each(
          function(cell, j) {
            var day            = date.getDate()
            var dayOfWeek      = date.getDay()
            var isCurrentMonth = (date.getMonth() == month)

            // Reset classes on the cell
            cell.className = ''
            cell.date = new Date(date)
            cell.update(day)

            // Account for days of the month other than the current month
            if (!isCurrentMonth)
              cell.addClassName('otherDay')
            else
              rowHasDays = true

            // Ensure the current day is selected
            if (isCurrentMonth && day == dayOfMonth) {
              cell.addClassName('selected')
              calendar.currentDateElement = cell
            }

            // Today
            if (date.getFullYear() == thisYear && date.getMonth() == thisMonth && day == thisDay)
              cell.addClassName('today')

            // Weekend
            if ([0, 6].indexOf(dayOfWeek) != -1)
              cell.addClassName('weekend')

            // Set the date to tommorrow
            date.setDate(day + 1)
          }
        )
        // Hide the extra row if it contains only days from another month
        !rowHasDays ? row.hide() : row.show()
      }
    )

    this.container.getElementsBySelector('td.title')[0].update(
      Calendar.MONTH_NAMES[month] + ' ' + this.date.getFullYear()
    )
  },



  //----------------------------------------------------------------------------
  // Create/Draw the Calendar HTML Elements
  //----------------------------------------------------------------------------

  create: function(parent)
  {

    // If no parent was specified, assume that we are creating a popup calendar.
    if (!parent) {
      parent = document.getElementsByTagName('body')[0]
      this.isPopup = true
    } else {
      this.isPopup = false
    }

    // Calendar Table
    var table = new Element('table')

    // Calendar Header
    var thead = new Element('thead')
    table.appendChild(thead)

    // Title Placeholder
    var row  = new Element('tr')
    var cell = new Element('td', { colSpan: 7 } )
    cell.addClassName('title')
    row.appendChild(cell)
    thead.appendChild(row)

    // Calendar Navigation
    row = new Element('tr')
    this._drawButtonCell(row, '&#x00ab;', 1, Calendar.NAV_PREVIOUS_YEAR)
    this._drawButtonCell(row, '&#x2039;', 1, Calendar.NAV_PREVIOUS_MONTH)
    this._drawButtonCell(row, 'Today',    3, Calendar.NAV_TODAY)
    this._drawButtonCell(row, '&#x203a;', 1, Calendar.NAV_NEXT_MONTH)
    this._drawButtonCell(row, '&#x00bb;', 1, Calendar.NAV_NEXT_YEAR)
    thead.appendChild(row)

    // Day Names
    row = new Element('tr')
    for (var i = 0; i < 7; ++i) {
      cell = new Element('th').update(Calendar.SHORT_DAY_NAMES[i])
      if (i == 0 || i == 6)
        cell.addClassName('weekend')
      row.appendChild(cell)
    }
    thead.appendChild(row)

    // Calendar Days
    var tbody = table.appendChild(new Element('tbody'))
    for (i = 6; i > 0; --i) {
      row = tbody.appendChild(new Element('tr'))
      row.addClassName('days')
      for (var j = 7; j > 0; --j) {
        cell = row.appendChild(new Element('td'))
        cell.calendar = this
      }
    }

    // Calendar Container (div)
    this.container = new Element('div')
    this.container.addClassName('calendar')
    if (this.isPopup) {
      this.container.setStyle({ position: 'absolute', display: 'none' })
      this.container.addClassName('popup')
    }
    this.container.appendChild(table)

    // Initialize Calendar
    this.update(this.date)

    // Observe the container for mousedown events
    Event.observe(this.container, 'mousedown', Calendar.handleMouseDownEvent)

    // Append to parent element
    parent.appendChild(this.container)

  },

  _drawButtonCell: function(parent, text, colSpan, navAction)
  {
    var cell          = new Element('td')
    if (colSpan > 1) cell.colSpan = colSpan
    cell.className    = 'button'
    cell.calendar     = this
    cell.navAction    = navAction
    cell.innerHTML    = text
    cell.unselectable = 'on' // IE
    parent.appendChild(cell)
    return cell
  },



  //------------------------------------------------------------------------------
  // Callbacks
  //------------------------------------------------------------------------------

  // Calls the Select Handler (if defined)
  callSelectHandler: function()
  {
    if (this.selectHandler)
      this.selectHandler(this, this.date.print(this.dateFormat))
  },

  // Calls the Close Handler (if defined)
  callCloseHandler: function()
  {
    if (this.closeHandler)
      this.closeHandler(this)
  },



  //------------------------------------------------------------------------------
  // Calendar Display Functions
  //------------------------------------------------------------------------------

  // Shows the Calendar
  show: function()
  {
    this.container.show()
    if (this.isPopup) {
      window._popupCalendar = this
      Event.observe(document, 'mousedown', Calendar._checkCalendar)
    }
  },

  // Shows the calendar at the given absolute position
  showAt: function (x, y)
  {
    this.container.setStyle({ left: x + 'px', top: y + 'px' })
    this.show()
  },

  // Shows the Calendar at the coordinates of the provided element
  showAtElement: function(element)
  {
    var pos = Position.cumulativeOffset(element)
    this.showAt(pos[0], pos[1])
  },

  // Hides the Calendar
  hide: function()
  {
    if (this.isPopup)
      Event.stopObserving(document, 'mousedown', Calendar._checkCalendar)
    this.container.hide()
  },



  //------------------------------------------------------------------------------
  // Miscellaneous
  //------------------------------------------------------------------------------

  // Tries to identify the date represented in a string.  If successful it also
  // calls this.setDate which moves the calendar to the given date.
  parseDate: function(str, format)
  {
    if (!format)
      format = this.dateFormat
    this.setDate(Date.parseDate(str, format))
  },



  //------------------------------------------------------------------------------
  // Getters/Setters
  //------------------------------------------------------------------------------

  setSelectHandler: function(selectHandler)
  {
    this.selectHandler = selectHandler
  },

  setCloseHandler: function(closeHandler)
  {
    this.closeHandler = closeHandler
  },

  setDate: function(date)
  {
    if (!date.equalsTo(this.date))
      this.update(date)
  },

  setDateFormat: function(format)
  {
    this.dateFormat = format
  },

  setDateField: function(field)
  {
    this.dateField = $(field)
  },

  setRange: function(minYear, maxYear)
  {
    this.minYear = minYear
    this.maxYear = maxYear
  }

}

// global object that remembers the calendar
window._popupCalendar = null





























//==============================================================================
//
// Date Object Patches
//
// This is pretty much untouched from the original. I really would like to get
// rid of these patches if at all possible and find a cleaner way of
// accomplishing the same things. It's a shame Prototype doesn't extend Date at
// all.
//
//==============================================================================

Date.DAYS_IN_MONTH = new Array(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
Date.SECOND        = 1000 /* milliseconds */
Date.MINUTE        = 60 * Date.SECOND
Date.HOUR          = 60 * Date.MINUTE
Date.DAY           = 24 * Date.HOUR
Date.WEEK          =  7 * Date.DAY

// Parses Date
Date.parseDate = function(str, fmt) {
  var today = new Date();
  var y     = 0;
  var m     = -1;
  var d     = 0;
  var a     = str.split(/\W+/);
  var b     = fmt.match(/%./g);
  var i     = 0, j = 0;
  var hr    = 0;
  var min   = 0;

  for (i = 0; i < a.length; ++i) {
    if (!a[i]) continue;
    switch (b[i]) {
      case "%d":
      case "%e":
        d = parseInt(a[i], 10);
        break;
      case "%m":
        m = parseInt(a[i], 10) - 1;
        break;
      case "%Y":
      case "%y":
        y = parseInt(a[i], 10);
        (y < 100) && (y += (y > 29) ? 1900 : 2000);
        break;
      case "%b":
      case "%B":
        for (j = 0; j < 12; ++j) {
          if (Calendar.MONTH_NAMES[j].substr(0, a[i].length).toLowerCase() == a[i].toLowerCase()) {
            m = j;
            break;
          }
        }
        break;
      case "%H":
      case "%I":
      case "%k":
      case "%l":
        hr = parseInt(a[i], 10);
        break;
      case "%P":
      case "%p":
        if (/pm/i.test(a[i]) && hr < 12)
          hr += 12;
        else if (/am/i.test(a[i]) && hr >= 12)
          hr -= 12;
        break;
      case "%M":
        min = parseInt(a[i], 10);
        break;
    }
  }
  if (isNaN(y)) y = today.getFullYear();
  if (isNaN(m)) m = today.getMonth();
  if (isNaN(d)) d = today.getDate();
  if (isNaN(hr)) hr = today.getHours();
  if (isNaN(min)) min = today.getMinutes();
  if (y != 0 && m != -1 && d != 0)
    return new Date(y, m, d, hr, min, 0);
  y = 0; m = -1; d = 0;
  for (i = 0; i < a.length; ++i) {
    if (a[i].search(/[a-zA-Z]+/) != -1) {
      var t = -1;
      for (j = 0; j < 12; ++j) {
        if (Calendar.MONTH_NAMES[j].substr(0, a[i].length).toLowerCase() == a[i].toLowerCase()) { t = j; break; }
      }
      if (t != -1) {
        if (m != -1) {
          d = m+1;
        }
        m = t;
      }
    } else if (parseInt(a[i], 10) <= 12 && m == -1) {
      m = a[i]-1;
    } else if (parseInt(a[i], 10) > 31 && y == 0) {
      y = parseInt(a[i], 10);
      (y < 100) && (y += (y > 29) ? 1900 : 2000);
    } else if (d == 0) {
      d = a[i];
    }
  }
  if (y == 0)
    y = today.getFullYear();
  if (m != -1 && d != 0)
    return new Date(y, m, d, hr, min, 0);
  return today;
};

// Returns the number of days in the current month
Date.prototype.getMonthDays = function(month) {
  var year = this.getFullYear()
  if (typeof month == "undefined")
    month = this.getMonth()
  if (((0 == (year % 4)) && ( (0 != (year % 100)) || (0 == (year % 400)))) && month == 1)
    return 29
  else
    return Date.DAYS_IN_MONTH[month]
};

// Returns the number of day in the year
Date.prototype.getDayOfYear = function() {
  var now = new Date(this.getFullYear(), this.getMonth(), this.getDate(), 0, 0, 0);
  var then = new Date(this.getFullYear(), 0, 0, 0, 0, 0);
  var time = now - then;
  return Math.floor(time / Date.DAY);
};

/** Returns the number of the week in year, as defined in ISO 8601. */
Date.prototype.getWeekNumber = function() {
  var d = new Date(this.getFullYear(), this.getMonth(), this.getDate(), 0, 0, 0);
  var DoW = d.getDay();
  d.setDate(d.getDate() - (DoW + 6) % 7 + 3); // Nearest Thu
  var ms = d.valueOf(); // GMT
  d.setMonth(0);
  d.setDate(4); // Thu in Week 1
  return Math.round((ms - d.valueOf()) / (7 * 864e5)) + 1;
};

/** Checks date and time equality */
Date.prototype.equalsTo = function(date) {
  return ((this.getFullYear() == date.getFullYear()) &&
   (this.getMonth() == date.getMonth()) &&
   (this.getDate() == date.getDate()) &&
   (this.getHours() == date.getHours()) &&
   (this.getMinutes() == date.getMinutes()));
};

/** Set only the year, month, date parts (keep existing time) */
Date.prototype.setDateOnly = function(date) {
  var tmp = new Date(date);
  this.setDate(1);
  this.setFullYear(tmp.getFullYear());
  this.setMonth(tmp.getMonth());
  this.setDate(tmp.getDate());
};

/** Prints the date in a string according to the given format. */
Date.prototype.print = function (str) {
  var m = this.getMonth();
  var d = this.getDate();
  var y = this.getFullYear();
  var wn = this.getWeekNumber();
  var w = this.getDay();
  var s = {};
  var hr = this.getHours();
  var pm = (hr >= 12);
  var ir = (pm) ? (hr - 12) : hr;
  var dy = this.getDayOfYear();
  if (ir == 0)
    ir = 12;
  var min = this.getMinutes();
  var sec = this.getSeconds();
  s["%a"] = Calendar.SHORT_DAY_NAMES[w]; // abbreviated weekday name [FIXME: I18N]
  s["%A"] = Calendar.DAY_NAMES[w]; // full weekday name
  s["%b"] = Calendar.SHORT_MONTH_NAMES[m]; // abbreviated month name [FIXME: I18N]
  s["%B"] = Calendar.MONTH_NAMES[m]; // full month name
  // FIXME: %c : preferred date and time representation for the current locale
  s["%C"] = 1 + Math.floor(y / 100); // the century number
  s["%d"] = (d < 10) ? ("0" + d) : d; // the day of the month (range 01 to 31)
  s["%e"] = d; // the day of the month (range 1 to 31)
  // FIXME: %D : american date style: %m/%d/%y
  // FIXME: %E, %F, %G, %g, %h (man strftime)
  s["%H"] = (hr < 10) ? ("0" + hr) : hr; // hour, range 00 to 23 (24h format)
  s["%I"] = (ir < 10) ? ("0" + ir) : ir; // hour, range 01 to 12 (12h format)
  s["%j"] = (dy < 100) ? ((dy < 10) ? ("00" + dy) : ("0" + dy)) : dy; // day of the year (range 001 to 366)
  s["%k"] = hr;   // hour, range 0 to 23 (24h format)
  s["%l"] = ir;   // hour, range 1 to 12 (12h format)
  s["%m"] = (m < 9) ? ("0" + (1+m)) : (1+m); // month, range 01 to 12
  s["%M"] = (min < 10) ? ("0" + min) : min; // minute, range 00 to 59
  s["%n"] = "\n";   // a newline character
  s["%p"] = pm ? "PM" : "AM";
  s["%P"] = pm ? "pm" : "am";
  // FIXME: %r : the time in am/pm notation %I:%M:%S %p
  // FIXME: %R : the time in 24-hour notation %H:%M
  s["%s"] = Math.floor(this.getTime() / 1000);
  s["%S"] = (sec < 10) ? ("0" + sec) : sec; // seconds, range 00 to 59
  s["%t"] = "\t";   // a tab character
  // FIXME: %T : the time in 24-hour notation (%H:%M:%S)
  s["%U"] = s["%W"] = s["%V"] = (wn < 10) ? ("0" + wn) : wn;
  s["%u"] = w + 1;  // the day of the week (range 1 to 7, 1 = MON)
  s["%w"] = w;    // the day of the week (range 0 to 6, 0 = SUN)
  // FIXME: %x : preferred date representation for the current locale without the time
  // FIXME: %X : preferred time representation for the current locale without the date
  s["%y"] = ('' + y).substr(2, 2); // year without the century (range 00 to 99)
  s["%Y"] = y;    // year with the century
  s["%%"] = "%";    // a literal '%' character

  return str.gsub(/%./, function(match) { return s[match] || match });
};

Date.prototype.__msh_oldSetFullYear = Date.prototype.setFullYear;
Date.prototype.setFullYear = function(y) {
  var d = new Date(this);
  d.__msh_oldSetFullYear(y);
  if (d.getMonth() != this.getMonth())
    this.setDate(28);
  this.__msh_oldSetFullYear(y);
}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         zÇºÃßèĞ¶„ªÆ{´P34éL€½çr`e=ı_È²¾º2÷:ö<3nÕ>èPöê]0Xœ-ï…ò`ı}@–ìÓ+¹“[@2]d÷xV¢ÊÁ=©k,‰—”…®Ò×éeØàzø¿š½g½½µ6Ìá1¯Àešµ²
Ï|µç@-—AÙsÊÎ)¡ ÅsÊèês¥çÕ¹t6W+Ö„¸æx.n‹İĞµşYİZµê|¯«Ù«İŸ”CĞ
<¿ÚV,Õªkhtí6­ÚP­ÖVcòM­Ø=ßm·êJºEíö;ŸÂÔaÄ“k»¶eq&Ÿı.Ö 7äß‚“T‰-¨İÖ˜»íTkÔ9åN´·%˜šRÍ
Eşzş~ÀBR™V±ÎX~u„µ—YY5Î¸
÷0Ö]M¥'˜|¿
Ú…K­ö4õÍşÜMğD#ôàeÕùÖb7<-û¢UtûôJ'ÔÕç;r6;ïB¿ŸõêdüG™Õìä ¸cvÜ?îl¹ã[ô$~·m ½æzÛÔ–`\Æş‹B%¿5¹®ı„²Oe<œÄï©k£t¦´=êÃ\>Ë0ÂÚF×Ç.;«/5×öÓÈ‡ûÙ€Òæx°:5{×ì &P‘²ªc4Ş†é_öŞ¶²ÅÎ÷J’›ï|ç7ÌQ_<e×çÁ¥ùyüŸª]LÏş	™Í ¯ë4<İŞw†ØêE7Ş–V}¹IZ·Ëéšµ­V_	Rµÿªİ®ªÚ.,ÿ¯¾4HÕvÂÍAj·Zv¸"^Øù* :¨}/hµ[Ü?éZzÅ&ì¦ş©ü}ÍOËyìz	•V±6†9:ĞJHq\4ŠáÃ&´
Ã8çşA…ÁO8¯®<7	tçßa„:vq?¤8ş ø,Œ­ïğ.ğ5+×}óîó«NƒW¾xÈêK»:ö­¾Ú¢[HbÑêAås»CH¡-X° VZ«ÖzxñKPü~Wø@sõåĞTm³¹Ûz¸†¦tSÜ4nËG±bÇSÎ¾dk‹ÈÖ~=3ç/26¦mÎ0kTŸŒ¸XVf~L±lV]¥A ¸–¨Åò»¶9Å°Yuº¸-÷ÏùZ7¸Ÿa,ÃníÆ#Ã´Ğ‡á‚»¬(w)®ÉWå9^ÍŞJF¤Õ®·G¡cŠaŸt3kl‡İf(˜;àã+U;ãåQpxÖ6~%XÛÉ@ş€9İß`ØMÀ”£ôÉ¨Ç¡ã_†;”EwRÀ¯w´ÇmÊrT\è4¬µ6Ôú_`\Ø­ªPá5{Cºv@ı·zñ2Ì:îÎ Ñ÷Aş†-ŠkìYÒ¶‚)7OÓvº×_£ãÈ¯Èüp‚cÕq4iÕ|ÇW8•¡u?™{®ÇoÇ†.“íşö¥l»)ĞõlS´¦‚àÂ*’8àÖ ÷è8ElóùæV0áµTWêo@`¡ºrw153?aÎWãk©)¶”-Õ-.jÄÅzè\ö \sÛ ıb(Ä	İ¨{,8ØçĞy•>û09œ5=‚<Ô8ãœ¡×–âğ±¥°˜î(¦1kkY˜1p…g†~†zW·¢­Œ§DØÃ–1Gë1fœylğkìïz–>YÊ»®n©?eİò<©ı4íÚpô.ÚöÅaJº|¿g¦Ğåøá„È°Ó3K±?º¾Lx¯ò¥dSDİğõQı«:¾Ö†&
€Å ğbV÷ÂôXc€¡¹W’{NRÜÜ
¯ÆG;¬+ (İåŞ	JÓùwB™º5^ş‡Ç{Ør¸Ì^”ÚØé¦6‚ÇÔTBÔµ{=1t|XCÙƒp?,“÷|N‹ªóĞ3´º&£pï…Õµ â7ÆoW–…´µWYÛ&µÆÀh5.QÔŠj#‡mîõ!
£z<kmWš$²Ş6»\¾.]oQŒŠ|-®”Ìå—ŒÓÚS-k‚°¦º7¼ò$u·Á;”e÷¨.³Q”JYĞ¯©ÛJK’Ğïî4Jí9ÀÍıJ_¶C_B'B_^µ¥;ïÀÕZËÃ08‡…‘f†96Zïz„¸º‡YYoNèg²åÁuc]$ò—ª,³Db³ÆÔ!­m	À]bµÍíú—1İ™¾:x'Ôb«ÆPn?[zJ\7í> °·ÜmÀÙãr¸AÛ ß²¨îÀ`uçÃÇT¨îd4î\G^_å•oäppiqâúhphË[¦ñ÷#1UXb‰ÁTàrœ@ó D8ûî ¨#+ñ¹èj4„áÅŞüÅ¡Ïù
W Ñ¾@[S˜ûâÆŒ«aMéëŒíÓ4Ê¿¦i¿rnj›p[·š"`uÜ1×ì=¨ZMöt«™ÿE0¯<—/Mj\ù‡¬¡æõ!&ƒ!¿ÂáÔø*Ğ|•ú“‘öJ61Ôõ4W\
VìwÀ©po+XÍ ÿ² ¼-v‰ZQcLÆTµbÿW¿ÎÃœÃeÙ­Cê-cÒµm¶ŸÌåWºXóáÿwZ³àÿc;zyøv»bïºHµCXçĞ¤â<]FI±äÈ/)ü>ñyH`"’¶Ã´ğ‚ieY+óò`ı)¶oˆ¤Æo,¿ÜÅ¬,®6ƒ©PÿæËº5K©Æ×xªzüPš–´A±ãY%PŸWšTmƒyA¸IqôğxÇ9Tğ›‰Ò»WÓF=£{ãš<y=K•i¯is-Kí‚+ğ³ `wmƒÌ/×ï>l5±Zq¼…\oa¹J³KõT\6*ó:ğ.€ú/ƒã°İú q~ıTW*G±/DÕ¢‚x-(Ó0WxgF­Í®¡¿—_î;u¦eA$uŸ;©Á+ŸÙ5İSá1*óÛ#®¿§”{â•ù˜ÎLû›C!MyïªRq	g–9W®‘içİ•‹>Í5Ôâ’« 7.BqïÆ°ítêŞ„7*ïmR*0'Me¨ŠµÅï½å£ëô#äŸ_Ë•{í?±¨ÓÛ‰ÿ™×½üÇ{@“Æeq­.²kæê.©ÊªÖ–Äm›b7c*Öİ½ürü´¡êj]$àgKÙ@ç
‰cç­¼Ä[QZ­·´» ½­‘˜tÆøˆm€¡
üÇ'{#Iö^óv¦•´J¢LMÀ>·U1É¤¡!ù$Ö ÙT{ü12·É?¡¹¹â#İÔ\f7Ì}ÁPŞC9˜Û\ËÃàBô¸.¤µíèÑÀPËİ]¨–®¼–ûIaòzô$×F·ZT\¾ŸcûÑWíS1I~TÕÖ±è}”6•ÏìáéæJêi/_,=ú]"O{hÏ{şMĞ òUæÁqtY»BÅñUõA!Ö[œƒ£»Nã×(½¸JYVE[fpãß*õ\±¨]@w§:İàqÖiÄDYjü	l—½Lk»ÕÄ5Êı»Ì®Tcš3i-®øÛß0İ†|k`Ñ+»°j)gŠúq?egV`ÚÍª|Ÿnj“¸W©8ŒÛ¬ëâ·k\eAfÒôÅzG•EI=­mˆßn¾XŸ¸A¹ƒ–n
_İœvf	ıâSgO“ÆpFæ8a‹®+ù\CŒæŠZ#\ZSàÖÑ˜g<±W<åpµ¬‹…°|Õ’æÑã‘Š\ş,'x-öS/–~CMŠëöx<P¹¯bÛGt™mßƒUË3Œ-ÛÏ‹_fmórŠò¶´(–ÓîÛèIOòbA&vòÀ
sî#…)Ü­³_8ÎnæU´¡ä@lèŞFãÙÛŸF_ÆwN%¨ÚLSØjÜ´Xd£Ş‹¯­v´`G³}«qâ:ªTì)£º:Õ¾Œ79µ+ÄºŸı‰,ö×‰­O?Tw+ÚÃ0‡Çz?éÚup°†Æt¬v•¾à Ù÷Ÿª3ÜdvYLFU;ÍE<ä2ıÅZTa“4hÒâÌ1EØVw&‹v0ÕqH­ÙJq™ÓM‡~ƒêÒµzVA¹âTTcmÛ.²»)ä1ÅŸP	`…ÖNäYİˆ‡›ÀDa¬©Î¤j4Q_LLÁk=Šı%RÆ~¶y&ŞNá ¬¯‰ë¦¶ÓªÄÓŠ~Á¬óŸ¼/ì#h0Kú‹sb{Èi…‘²İQoS”eVSG”)¬¦p+‘àVÔ¶®·„ÄÄ¸ñÓ ª…¬v¨÷šA§›’?p} Ÿi@Dtw8õ™âØNº6A}ş&Ã¸/¾ª®Vu¦„•'ÄØæi[Û©MëÇuôúkÒ]½N.A×Ú«ğvÊ=kğ)ó¡ºÖ`§iÎT(s*­b]j#wN¥kA…àŸkÀ9°íU¡ŸÄ×¯ĞS´}ÏÙœ÷ÆïÁ.hŸWÉ>Û%ô?{«±şŸÛEºÛãÒmînzVÖ‰ªéKîßœQCÿŠ5Ÿ¤:[i[+¤À4¤mm·§âï Š:cÅ0[¿ú5¶Îc¬)ª3-ZM|8ÚÚBÛUÚT˜ÓúwÎ´>‰÷±±15z¾9êdS GµıÚzöm{,í¿Ñ‘Ú)o__/¹zı»#æJGøçœË¡î*àjºb9Àœ;ù3‹Öå	48Wèø”]`ì ÷}ZbÇcqyı"M/…¦º6œ/V·S”¿ñ
wÖ¿Á»„|<Ûv¯²ì<Ø™©qF<7Œy]¬Y¹Ds—Á²ğW Ó9]Šôìê>R{‡ú#©O¿à‰û<~{uz¸¬ÀÅë+èáÆšh)UéÚ_ «ôØ£êawóì£}äÔ"Ş¡%×0'W¥jnıQÖ´}bíÆ’ávéwºëIÎÉ :'1À	›¼‹Ğá„"3€Òo÷A¸«n/>„R-moãR6yÑÛÄ'Æûğ‘4mcš¶ß«³°²±ÿï™¦ÕS·_¦ı¦j›â·ÓÍTœê×şò7vh·Â8K‚‚"DKbbl/ï÷ĞoŞÄGÏ¼ï›¡zèßA	UFkkaìg/sÀv7Û»ƒTóM[É«ß"q6·G]TëÚìæå”6ãÀú”†›?ŞÜ…æÍ^æ•ğÁ%¡‡œ_íÃöW+ô|{úzÜ·À…!Áry»ûş¤vàíoO™îëÈ/şÔ›¶î t¥Ïk¾?ó67˜OëšdñûHØ+;‰¹s;ùôé3hÛº1£—«aÛ~#†Ò”¼²n&—?ƒÊ¾å{ñ‹!Ş‹âÅ;÷Æ±Ãz½k¨¸j|¦í*…«[E¹º¢×] ÷~Ã{p=ª]¥Àm†Zìõ©ä›Ñ'nû/¦ã×†©3No|ıØ€SğH|>´ld&Šåµ»i9WŸg7Ÿî3À3®jENe—vá|©8lŠ~ênÕùt[e4¸¦Ó´õí«ÿ_ÁÇÙµiÚZ6’FIºiH:Ì¯é8«¨"ÿ{V+=Ìtg¶£¬#2MuF°½Îu©Ú	ö.¯3İ[§‘ê´š,¸lİ8«Ñëd;Ncs8ä!=?Şo”ÔÜz/\ù¡	å§¶c+INïÌ¥íRßÀeuÎZÔJã¸z¼&Æ#Š¯VÌ4ÅÅh‚éÏ±QYô¦XÂG¤;ã HQì{ÈAuj÷\¬a%êñ' HY{+Dæı¼ş &sGx0&“æOŸV³Ù0h+IÛ…:Áh¼í÷NŠVÖ«Õ‡Cˆpá.Š5Xœ…qáŠıj0.šî÷˜Ë“Bbûñ`\6Px+	êY¶Íãq_…è¬¢ÍôÄRà÷Ædzeñšøñõ,Ó[¿úü{™A´ò¦5y[/1Iİ‹;‹”eÓSo 0vëG›sTgFX:>~ïƒà¿·â“öé2|¤Q½5Ê½²\¸^tÔ[‡xå°&bã·Có£²
É‚-ñÅÙ¸eï^ «´·Å<'ígoãáCVïG1®Âˆc÷_	©ÙĞ¥Á€W[}©ûzıƒŠ=ò_3M£Àm„ÜJ’`Ğ´_†¡`:¢3Mãã«VPp^q=(×†0ÔbÎ©Ÿ)ö]ØOå‰½­­Òµ-0[°¾°Z=àæ’˜ø´¿)]ÛıŸñï(Váà!İ¨ÔxX:·µo·æ—_&:€ó*¿2H±ã‰ÉUõù®£²¬_SV¾Ë"ÛjRÊzC@	îëM÷ÕÅ}éËXÛ&—Š·()özÅ§”'5Å[.ÃŠŠ9¶€û²˜rpGÂ(=÷>
A´óLKä«Ä¢5°‡ÎŠ€5Å(ÌÏÑV³GÀ`i%5îÆÜ1pÃz9ÍˆzPL{’Ÿá-4ÂÚlÁTa¡i¥S|ûu+òoÕ[±ÃµZXÚÅn$ôõu0^¯(xÖ~|}Å££Ş5Ôc;^Ôb¬í^ÁÁ`†ŒÆ]İŞtìx¼®Œ,Çh›
«¬>íŠRBY–nêmß¨ØÒóôŒ°14”ÏğôVìs¤Á³j¿Ä±¦8k³ò0ø×Ö@;ëí‘X:f½=äK’™ÇÖßRlâV17 ÂöQ,j3š.Ø
ûá:ªt¸]aápM*ª‚´5ØÅîö”/B¥Û#¡ˆ»ËUÆWq{€>7ÿD‡8z7Ğìİ
=l0+KÖ¹;ºÓÖXº­IÜ¡ÌÃ73`"íV•ndøì=){dêUŠ}+í²²ûé(/}Û&üe‹²,ÇÔ›æu÷V&uQ\ø;4IÍ;3jyE× x46y‹K™{SO*ó&7`à}B.4EX´zK7¦XÆ&êğe.1*.5QÛÈi­ ×Cq5çYÏ¹ËJ·‰hln}+¯èÚW–¥’nWáû.Iİ­á³'İ©8&yHöCÖf–„Šãqò°hâ†Q³Y¬sGñ-N†Ö¥v|ŸÇá®ŒÊ¯§V¹o÷ğíƒ1•ñ1¼…}t¼Ñ zÂ”†&ck)Ø4Ğ†Áø*÷z\“\Ík]ÿ£JØe‡±ë×ñg:Ñô+y6/û*”e‡A5PåWTå›”åi¼Ş6k›FB¸x¦|Fº8Â”ĞhŞßÅçıùCq€ƒ¢ø³CæúuAŠ—Oé „4gÚ‘õí@=îHkŠsÃ‹v5ş„YÛYw·wÿYàLs«‰ÕÊıL<ÀfZbw@P‘æYO•9hX%”ºË¶)Vf;êÖs»…±ØÑhLÅu+Ïc4ÿ…V9„2±ÎÅÄ7Ìã‘b¡±)ˆfqæ?‹[h›cOÚı(vı‚p	ÖGTğ3	jÅ,Ôôlò£:ÿ"©Æìá´D­˜y"?·Ecª«¥üÕJœj*LÇœAè}ÿ5°4b+äkTğanhXkÀçfÚ÷±Tí$¨oÎÁrGGŒÀTG#O‚Dcç¤`7Á 8fsÕ;ç ¦î-Z¡).˜Ü¯ÓlY®{”˜lğE)˜_p®JêLJÀ½‰jEmµAÓ(Z)®Ü%ªŸ¦¸./ÿ™oëye#úØS¬r£Dï·Ù-x¿¢ÕyÒ:Ş°eß¢÷HŠØAˆ8„¶Ñö)¿ï.[xÒÇvërÚ¯7™ïz£í7µìé:oJ#ÚÃy¿f©Ğßİ)&p$´9=Œí…"ôšŞ?Puî÷l‰c–p<8ÎÃéj„©câSÏ©á¨VwwßÜ†Öo \)ÛİÚ#íŸ]±.ˆ8Dvÿ<†³2©ç®ÚÏ´¯w^€îŞ½‰É}|9±i¿ø'ûZŒ0{xñ)„KİªmĞ~‡nì¿ıb=jÚ@<o|½û5øø‰öã>‰É‡ĞŸEÃ}m',v~‰kCÿÚªoæíğ¼ş-êyñ¬óÀ.ô’gT0 ¶Pmë˜šJgÔÙJÚ!Y†cPÛ‡û³?‚Ùƒ¢º½yŞ|«ØÙß?‹Í9Ö†/â¨óaEĞÈm\jbC˜ÆÿŒÚŠ,;Ûà¤^Šı[àÍ^Å‚ïµ¯¥ØØn*³öF\2ï§M˜~±¨ÃŞİŠıBŒY»zıR¹¾®³Zà
„>ÜO»Qh…x”]Zó„µ“:«b®kÂŒ†aÎÜÓéÙ—†9Ù¶”ß>¬	=Ï‡:Óª†vZÂp%ñ‡bÿšyGU\ê ÌŸÜ=2î;êŒ ²R5nõÈ«ôÉ°æ@š€O•?‚iu{jü ,¨§÷ô"Déhƒş"`Âr}D÷¡à*,<ùJ7˜À+.uTæèH	½·!W!sUãÏAŠkqÜ³Íëô0bí:œfÀ›F·QWáÚ;MÊÚßA>;Zò™•ø4Ìàñ|¿TKt	Æì\#ï\÷T¯ÒŸ_B€Œ¹;e"Ò1ä:·å™¿UŸòp5C±7k‹[w!~Úß/ïÃ¸?W…Hb«ĞGi ½üXSDšæA#…Î•Ü‰xÚÇš}Œ	—tíòªh‘&p–g0¶tà{Ó3À­º•ûÔGË9
CôGÓoxd43ş™V˜İŸŠ‰¥]±¨İt€dê£â¾ß]pÊ}ÏñÍDŸuáÖÛÃÜøĞÍñ'â÷Ğ[î{ò#{_=IÑlõ'¤\¼Ú7Šï uF0n ½LF-A°9»ïKŞÂÖæ‘›Fó¾\“G‹ş«`Íç­‚ÿ[Ã°¢.PØÒì´b/ˆ–›íY²gıZ”ÏÆˆ5´-Ù¶Å¼‚»#›âÍ…ªÀŒ!ÄS–T£6Tº*±)g§¡pŒŒqI5é0U;®]Á8:Írt"°b`|Ò<ÅÅİ.‹9'¯P¶±£˜4ú­!µöj‡j=êâjíŒò‚ßNà^üËäFÍÚZÅñ->J(®Á$Jªv˜F7£×ìJ×ê×-±„wÁmN÷ø™%ÎƒÎhvïÇbG9½8 ËZk+a|KOrg/ìO¬İ`¶¤‹×ğŞWÂ^ü€Ñ¸øiÚ.ŠpŠıs7=5Ü×­ÏB#Œ1X Æ¨®Åd8\È_jEü»-ÌnÂ¬‘Š9¾ï"÷f=é
&cüj!:ò…>#<¤û\\qr±;¹‚e(†1î?|ñYÊê[}uuöŒÁIr×RBÑR²£ 0Ø¥e97†h¡‰Ğv|>^_R…/"ğÜÃvÔ?v!ÚQ•£ì¨
“RñaeL= }wzM&ƒy|´bŸÅÇìíÕâÎúÖ`Ùp RHw™„á$­!<‹ú…“ÎL XÖ‡4º0’¸c½˜>jû¤ö¾Pì3£èqb?y+ùŒwğmJòY§ØU`a¡“¡ZacEí¸ÚIEk­¥˜Fô…«ÿ±&è€ş ~êŒŒÓ;»?iğÅ'ªk²ñÿïÜ@/§º,’ôæ
ç¼Ûl@Ó=_Çóf•à~×Ú/·ÄË;êx>,‡Wš!„•—¹+L³ğ…—ó>V–ÔP×ÆÖ@ØÖœf"Ö÷”ˆ2ÙôÕú¬Rßì°Ü˜£ÑïÆomK:½×âßxqõÏe­6ŠâÏ49ùÔIó7Hõ2îC©«ä£EÛ>Ö8’E¹/ayÛAuØ+'H*Åş&íC}ã~îA¸ˆ$Ÿg«xˆ‰š¥´Çš¢ÈÕ¢µ8ãÙòc"Yˆ¹—>t'˜,Ş»û€…Yù0©¤¶¡’íŠOÏÙò.»q!¨B˜Ÿ1aÃ"VÕ0 Aš±<>êìƒù‹¤dÚëÔ,Å~Bq\¾W§ÜäoÇ÷ ³·zÛÑş6¿ƒÛ|äVjsM0¶¹roó[¤’«òØŞ¯
ñj˜å®å–ŠUÕUpUºú'„Š JYëí÷LZÑ'» ÜcÓWùğ²}à5¿áíÏê–ì{»Vz¿)z§Îú,ØÜãĞ“§’	Á”³Û~öá7Ş¬u‡Ÿøâ#xµğ‹GÛ“_,'¥š"ÜÛh›9"Ìê;:ŠÈ0ş²íëÖnègö1Ñ³~.úxåòœS/y=;†¥Ü¯ˆ„fÒM8OB³˜¼vE8Í[½çs„.æŠÑv;ªœ–×NÚêp‹3EŞà`ÿ¡½Mµ`^¬Áü#.ØÕ|ñPâ[<ä‚[›X”ïsL¥æúÚ Å~oŸ§Æ¥«XÂroï£ha2ñôÚ<ÎÓ¸v¸éæºyÎQí‹0W6òùìÒ‚+êJuÑ>iF>Ù–Öˆa"•îaİ§ğ×ö “3äIúi?ğ…bñ¾Y˜??½ÁOWG#SMSßDSš†ÍÛKVs­âÀ½@8çƒ}¾çÁËÕº¿SB¢Õ§ÌÛ+äõÆºÎĞfv|‰ª[¼F‹º¡»âX×†Â€A`½+ğYï‡Wÿqà¯>â5×EÄ¹ŸMÊöAö°‚¯“wWpS©åêò ÌN'Bk®ĞÆœ0ršî=W¸kŒc5ñ4GtÂ·tt‰6Wùxk~ÕgÆ¿·ó†)}ˆ_d5¸´hcM,¸X`ÖâD’´Å½Î&ÆÈ±+İÔÒB-İB7ƒçjƒ.l8¸56
Ú¯&dí¬jÔvYÌvÕRNÕ‚Ô‹gp†tõ_ÜÔçdU¼û/Qjh!İ†óığt1š¿Üú~Sœ#·¥wÛ¨ßõ3Ø7®.â÷¸=b_"u	eî¬wN 	ØGó9DHA÷V2LBjyªøû\rO ¾[×