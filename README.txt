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

Usage instructions

If you want to send emails using this library, create a new RJMSender
instance, supplying your computer's fully qualified domain name as ehloHostname.
If you want to send all messages using a single relay mail sever, call the
setSmtpServer() method to configure an smtp server. If an smtp server is not
configured, the sender instance will attempt to configure dns resolution to
determine MX records for recipient addresses using the file /etc/resolv.conf
Nameservers can also be configured manually using the setNameServer() method.


Once the RJMSender instance is configured the method sendMail() is used to
send emails. If you want to send messages with multiple recipients, use the
sendMulti() method.

STARTTLS support

If a server advertises support the STARTTLS mechanism described in RFC2487 in
it's EHLO response message, this library attempts to enable a cryptographically
protected TLS session to the server before sending any email messages. rjmailer
does not attempt to verify the server certificate, a self signed cert will do
just fine. The RJMResult instance returned by sendMail() contains two properties
tlsCipherSuite and tlsCertHash that contains information about the cryptographic
algorithm suite chosen for the connection as well as an SHA-1 fingerprint of
the certificate of the server that can be used to detect man-in-the-middle
attacks.