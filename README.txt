rjmailer - The robust java mailer

This is a small library that is used to send SMTP emails. It connects to an
SMTP server, formats and sends email messages. It does roughly the same thing
as the smtp part of the javamail package, but using has several advantages.

External dependencies

This library has a few external dependencies. All such dependencies are
distributed as jars in the lib directory of the main distribution.

Logging

Since the rjmailer library is designed to be integrated in many different
environments I have choosen to output logging using the lightweight logging
facade slf4j. slf4j is compatible with most logging frameworks such as log4j,
java.util.logging from jdk1.4 and others. For more information about configuring
slf4j please see http://slf4j.org

Usage instructions:

If you want to send emails using this library, create a new RJMSender
instance, supplying your computer's fully qualified domain name as ehloHostname
and the name or ip address of your SMTP server as server name. Once created
the method sendMail() is used to send emails.

