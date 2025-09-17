#Introduction

This project is built to understand how Online payments work, particularly Stripe.
System Specification:
1. We have integrated Stripe using its SDK
2. Initially, we are accepting only Card payments
3. The flow is asynchronous 
---
How to RUN
1. Create an account on Stripe
2. Get the keys (Public, Secret, Webhook Secret)
3. Set them in the project's application.properties file
4. Save and run the project
5. Browse this link after running: http://localhost:8080/enroll
6. Enter details
     1. card number: 4242424242424242 (Demo visa card for testing)
     2. random CVC (3 digits)
     3. Card Expiry (Obviously a future date🙂)
     4. A 5-digit postal code

