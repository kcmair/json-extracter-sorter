# Overview


I chose DynamoDB as the database for this project because while I have some experience working with it, I wanted to take a deeper dive into how to read, write, and modify data using it.


This program is designed to read a JSON format file containing multiple pieces of data in each object, extract the specific pieces of data that I need, namely the date and sample code, and then write that data to a table in DynamoDB.


To run this program, go to the project's root folder in a terminal window and type npm start. You will be asked if you want to read, write, or delete from the database. To read or delete data, type in the date for the data you want to retrieve or remove. To write to the database, type in the path and filename of the file you would like to parse and add to the DB.


[Software Demo Video]()


# Cloud Database


The table in DynamoDB contains a key, which is a date. Each date contains a list of test codes that were submitted but failed on that date.


# Development Environment


To better understand DynamoDB's workings, I did a deep dive into AWS’s documentation on the DB and also watched and followed along with a couple of tutorials on LinkedIn Learning.


The libraries I used to create this project include:
- AWS Java SDK for DynamoDB
- Jackson Databind to read and write JSON to a map.
- Java.util for error handling.


# Useful Websites


- [Amazon DynamoDB Developer Guid](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/)
- [AWS for Developers: DynamoDB](https://www.linkedin.com/learning/aws-for-developers-dynamodb?u=2057052)
- [AWS Essential Training for Developers](https://www.linkedin.com/learning/aws-essential-training-for-developers-17237791?u=2057052)


# Future Work


- Add the ability to select a file from Finder/File Explorer.
- Add the ability to specify the path for the new file using Finder/File Explorer.
- Add the ability to read the data directly from the database instead of saving it to a JSON file first.



