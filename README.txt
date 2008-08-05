rjmailer - The robust java mailer

This is a small library that is used to send SMTP emails. It connects to an
SMTP server, formats and sends email messages. It does roughly the same thing
as the smtp part of the javamail package, but using has several advantages.

Features:

- Lightweight. The binary jar is less than 19k big at the moment, and the
external dependencies are kept at a minimum.

- Integrates well with Spring Framework. If you use interfaces in the mail
package of Spring Framework you can replace your current email sending solution
with rjmailer right away. The rjmailer Spring integration code is optional,
however and can be safely ignored.

- Rigorously unit tested. The package has been developed using a test driven
approach.

Usage instructions:

If you want to send emails using this library, create a new RJMSender
instance, supplying your computer's fully qualified domain name as ehloHostname
and the name or ip address of your SMTP server as server name. Once created
the method sendMail() is used to send emails.

