# Zerofox
This project is the course project for Security and Privacy in Computing, Fall 2016.

# Introduction
This project aims to detect attacks toward social network accounts in real-time. The goal is to build a Twitter Account-Takeover Detector by monitoring user’s tweets. We chose Java as the programming language for implementation because I need to train model of SVM classifier and use that model to evaluate each tweet. The following information will be recorded:
	1. Date
	2. Content
	3. Label (0 for good, 1 for bad)
And the two parts that implement the detecting strategy are model training and twitter monitoring.

## Tool used
	Twitter4J
	Liblnear

## Reference
For more information, please check out this [paper](http://weichengzhang.co/github_source/SPC_Project.pdf).
