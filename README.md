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

Here is an example session:

```
Block:
Created by: Miner4
Miner4 gets 100 VC
Id: 1
Timestamp: 1678203184775
Magic number: 175429572
Hash of the previous block:
0
Hash of the block:
015f3557109a7f55dacf9849e439540299f4b0a9f4dca3b21f562abf258cfb75
Block data:
No transactions
Block was generating for 0 seconds
N was increased to 1

Block:
Created by: Miner3
Miner3 gets 100 VC
Id: 2
Timestamp: 1678203184792
Magic number: 460368399
Hash of the previous block:
015f3557109a7f55dacf9849e439540299f4b0a9f4dca3b21f562abf258cfb75
Hash of the block:
0f0533950656350b53b7d8c6e4224fe1d8051bf28ad8cffe8d9eb9927bd1c9f3
Block data:
Transaction[transactionId=2, fromUserId=Miner2, toUserId=Sarah, amount=2]
Transaction[transactionId=3, fromUserId=Miner4, toUserId=Nick, amount=9]
Transaction[transactionId=4, fromUserId=Miner1, toUserId=Miner3, amount=6]
Transaction[transactionId=5, fromUserId=Tom, toUserId=Miner4, amount=2]
Transaction[transactionId=6, fromUserId=Nick, toUserId=Miner1, amount=3]
Block was generating for 0 seconds
N was increased to 2

Block:
Created by: Miner5
Miner5 gets 100 VC
Id: 3
Timestamp: 1678203185218
Magic number: 164181169
Hash of the previous block:
0f0533950656350b53b7d8c6e4224fe1d8051bf28ad8cffe8d9eb9927bd1c9f3
Hash of the block:
00cb6d00476f6c71979a3f85a73fc211cc9d1fb5d0a58931143ddef0576b2836
Block data:
Transaction[transactionId=17, fromUserId=Miner3, toUserId=Miner1, amount=6]
Block was generating for 0 seconds
N was increased to 3

Block:
Created by: Miner3
Miner3 gets 100 VC
Id: 4
Timestamp: 1678203185347
Magic number: 456737670
Hash of the previous block:
00cb6d00476f6c71979a3f85a73fc211cc9d1fb5d0a58931143ddef0576b2836
Hash of the block:
00041c4554df872404c1da58c04fa2736e2d4b2534efbc36176fe7ccc7997df6
Block data:
Transaction[transactionId=18, fromUserId=Miner5, toUserId=Miner1, amount=9]
Transaction[transactionId=19, fromUserId=Miner2, toUserId=Miner5, amount=4]
Transaction[transactionId=20, fromUserId=Miner4, toUserId=Miner1, amount=7]
Transaction[transactionId=21, fromUserId=Tom, toUserId=Miner2, amount=6]
Transaction[transactionId=22, fromUserId=Sarah, toUserId=Miner3, amount=7]
Block was generating for 0 seconds
N was increased to 4

Block:
Created by: Miner3
Miner3 gets 100 VC
Id: 5
Timestamp: 1678203185908
Magic number: 489632413
Hash of the previous block:
00041c4554df872404c1da58c04fa2736e2d4b2534efbc36176fe7ccc7997df6
Hash of the block:
0000850cf5f926b0ba9a5f0ec3f29352b6b69179872ed0117263ccce5aaadef6
Block data:
Transaction[transactionId=33, fromUserId=Miner1, toUserId=Miner2, amount=1]
Block was generating for 0 seconds
N stays the same

Block:
Created by: Miner4
Miner4 gets 100 VC
Id: 6
Timestamp: 1678203186155
Magic number: 856596222
Hash of the previous block:
0000850cf5f926b0ba9a5f0ec3f29352b6b69179872ed0117263ccce5aaadef6
Hash of the block:
00008bf071423bd7902e170391fc14c452a4781a87b47950d6f28b9dc6b9bdf8
Block data:
Transaction[transactionId=34, fromUserId=Miner2, toUserId=Miner4, amount=8]
Transaction[transactionId=40, fromUserId=Tom, toUserId=Miner2, amount=2]
Transaction[transactionId=41, fromUserId=Nick, toUserId=Miner1, amount=3]
Block was generating for 0 seconds
N stays the same

Block:
Created by: Miner1
Miner1 gets 100 VC
Id: 7
Timestamp: 1678203186376
Magic number: 399012062
Hash of the previous block:
00008bf071423bd7902e170391fc14c452a4781a87b47950d6f28b9dc6b9bdf8
Hash of the block:
000020d43a2a0964938941f08fc5cd103f223f889f0747425d8a9dcbfaf9e79a
Block data:
Transaction[transactionId=47, fromUserId=Miner3, toUserId=Sarah, amount=3]
Block was generating for 0 seconds
N stays the same

Block:
Created by: Miner1
Miner1 gets 100 VC
Id: 8
Timestamp: 1678203186704
Magic number: 189731053
Hash of the previous block:
000020d43a2a0964938941f08fc5cd103f223f889f0747425d8a9dcbfaf9e79a
Hash of the block:
00002f1294245399b34d3e1ea69f7ab981d8b5b1f6bcc10a0c8901575519b605
Block data:
Transaction[transactionId=48, fromUserId=Miner5, toUserId=Miner1, amount=3]
Transaction[transactionId=49, fromUserId=Miner4, toUserId=Miner2, amount=9]
Transaction[transactionId=50, fromUserId=Miner2, toUserId=Nick, amount=1]
Transaction[transactionId=51, fromUserId=Sarah, toUserId=Miner1, amount=5]
Transaction[transactionId=52, fromUserId=Tom, toUserId=Miner2, amount=6]
Transaction[transactionId=53, fromUserId=Nick, toUserId=Sarah, amount=2]
Block was generating for 0 seconds
N stays the same

Block:
Created by: Miner2
Miner2 gets 100 VC
Id: 9
Timestamp: 1678203187771
Magic number: 882537015
Hash of the previous block:
00002f1294245399b34d3e1ea69f7ab981d8b5b1f6bcc10a0c8901575519b605
Hash of the block:
000044420cbbbfa5e9ea6d7ac9e6665ee5dcd979bf62a5a86d761201a85239a0
Block data:
Transaction[transactionId=64, fromUserId=Miner1, toUserId=Miner3, amount=7]
Transaction[transactionId=65, fromUserId=Miner4, toUserId=Miner1, amount=4]
Transaction[transactionId=66, fromUserId=Miner2, toUserId=Miner5, amount=3]
Transaction[transactionId=67, fromUserId=Tom, toUserId=Miner1, amount=6]
Block was generating for 1 seconds
N stays the same

Block:
Created by: Miner3
Miner3 gets 100 VC
Id: 10
Timestamp: 1678203188288
Magic number: 940645066
Hash of the previous block:
000044420cbbbfa5e9ea6d7ac9e6665ee5dcd979bf62a5a86d761201a85239a0
Hash of the block:
00007ff2396e58d157b7acc985899f0f5e712a22a61c138056c388f08fe25dce
Block data:
Transaction[transactionId=73, fromUserId=Miner3, toUserId=Miner1, amount=7]
Transaction[transactionId=74, fromUserId=Miner5, toUserId=Nick, amount=10]
Transaction[transactionId=75, fromUserId=Miner1, toUserId=Miner5, amount=5]
Transaction[transactionId=76, fromUserId=Sarah, toUserId=Miner5, amount=3]
Transaction[transactionId=77, fromUserId=Miner2, toUserId=Nick, amount=4]
Transaction[transactionId=78, fromUserId=Nick, toUserId=Tom, amount=6]
Transaction[transactionId=79, fromUserId=Miner2, toUserId=Nick, amount=8]
Transaction[transactionId=80, fromUserId=Tom, toUserId=Miner5, amount=1]
Block was generating for 0 seconds
N stays the same

Block:
Created by: Miner3
Miner3 gets 100 VC
Id: 11
Timestamp: 1678203188383
Magic number: 460824785
Hash of the previous block:
00007ff2396e58d157b7acc985899f0f5e712a22a61c138056c388f08fe25dce
Hash of the block:
00007ce0da9775c283e4b149adfc468395ddc75204013450341f830dd58266c1
Block data:
Transaction[transactionId=86, fromUserId=Miner4, toUserId=Miner2, amount=6]
Transaction[transactionId=87, fromUserId=Miner1, toUserId=Miner5, amount=2]
Transaction[transactionId=88, fromUserId=Sarah, toUserId=Miner5, amount=2]
Transaction[transactionId=89, fromUserId=Nick, toUserId=Miner3, amount=7]
Transaction[transactionId=90, fromUserId=Miner3, toUserId=Sarah, amount=1]
Block was generating for 0 seconds
N stays the same

Block:
Created by: Miner3
Miner3 gets 100 VC
Id: 12
Timestamp: 1678203188446
Magic number: 430620573
Hash of the previous block:
00007ce0da9775c283e4b149adfc468395ddc75204013450341f830dd58266c1
Hash of the block:
0000da0d0a21b294bd3ac17d7409a21f86b8488794a866d0c55e57abcfd9901a
Block data:
Transaction[transactionId=91, fromUserId=Miner5, toUserId=Miner2, amount=2]
Transaction[transactionId=97, fromUserId=Miner4, toUserId=Nick, amount=10]
Transaction[transactionId=98, fromUserId=Miner2, toUserId=Miner4, amount=8]
Transaction[transactionId=99, fromUserId=Tom, toUserId=Miner5, amount=10]
Block was generating for 0 seconds
N stays the same

Block:
Created by: Miner4
Miner4 gets 100 VC
Id: 13
Timestamp: 1678203189349
Magic number: 164926535
Hash of the previous block:
0000da0d0a21b294bd3ac17d7409a21f86b8488794a866d0c55e57abcfd9901a
Hash of the block:
000047ded3d2beebc74ea4ae6b6bc0738f60a7af256125b87ac69146bc4519a7
Block data:
Transaction[transactionId=105, fromUserId=Miner5, toUserId=Miner3, amount=8]
Block was generating for 0 seconds
N stays the same

Block:
Created by: Miner1
Miner1 gets 100 VC
Id: 14
Timestamp: 1678203189694
Magic number: 816850930
Hash of the previous block:
000047ded3d2beebc74ea4ae6b6bc0738f60a7af256125b87ac69146bc4519a7
Hash of the block:
000045e5a2a124155ae4470775e1aa52e33cd425fe0c0432b38ba0bd6dbe35f8
Block data:
Transaction[transactionId=106, fromUserId=Sarah, toUserId=Miner5, amount=1]
Transaction[transactionId=107, fromUserId=Tom, toUserId=Miner1, amount=5]
Transaction[transactionId=113, fromUserId=Miner3, toUserId=Tom, amount=3]
Transaction[transactionId=114, fromUserId=Miner1, toUserId=Miner2, amount=1]
Transaction[transactionId=115, fromUserId=Nick, toUserId=Miner3, amount=5]
Block was generating for 0 seconds
N stays the same

Block:
Created by: Miner1
Miner1 gets 100 VC
Id: 15
Timestamp: 1678203189853
Magic number: 288734742
Hash of the previous block:
000045e5a2a124155ae4470775e1aa52e33cd425fe0c0432b38ba0bd6dbe35f8
Hash of the block:
00001a36930e9bf9e694c528702af3f17d15bf193f6a7398b14c26f8748fe197
Block data:
Transaction[transactionId=121, fromUserId=Miner3, toUserId=Sarah, amount=3]
Transaction[transactionId=122, fromUserId=Miner4, toUserId=Tom, amount=5]
Transaction[transactionId=123, fromUserId=Miner1, toUserId=Tom, amount=7]
Transaction[transactionId=124, fromUserId=Miner2, toUserId=Miner3, amount=2]
Transaction[transactionId=125, fromUserId=Sarah, toUserId=Miner5, amount=6]
Block was generating for 0 seconds
N stays the same
```
