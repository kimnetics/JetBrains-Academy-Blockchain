# JetBrains Academy Blockchain Project

An example of a passing solution to the final phase of the JetBrains Academy Java Blockchain project.

## Description

This project is a command line application that simulates a running blockchain environment.

The application creates a thread pool of users that transfer random amounts of money to other users by adding transactions to be added to the blockchain. Users are given a starting amount of money to spend.

The application creates a thread pool of miners that take a group of transactions and attempt to find a nonce that satisfies the requirements necessary to add the transactions in a block to the blockchain. The miners are competing against each other to find the nonce. The winning miner is awarded money via a transaction on the blockchain.

A Blockchain Manager dynamically adjusts the solution complexity to keep blocks being added to the blockchain at the desired pace.

The application has several pause loops to keep the data flowing at a rate suitable for the automated tests to be happy. The solution complexity logic also has a maximum value specified to keep the miners from taking too long.

## Notes

The relative directory structure was kept the same as the one used in my JetBrains Academy solution.

The program logs to a file called "blockchain.log" in your home folder.

Among other information, the logs have details about all the transactions that took place. They also show the final balances for all the users in the system. Each user starts with 100 money units. Miners are awarded 100 money units for each successfully added block.

The final phase of the project required the application to demonstrate the addition of 15 blocks to the blockchain. The application stops after 15 blocks.

The application can be started with the following command:

```
java blockchain.Main
```
