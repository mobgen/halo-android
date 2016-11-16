#!/usr/bin/python

import argparse
import requests
import sys

def main():
  parser = argparse.ArgumentParser(description="Parses the report files and sends it to slack")
  parser.add_argument("-lines", help="Provides the file where the line count is stored as 'total source comments'")
  parser.add_argument("-coverage", help="Provides the file where the coverage is stored as 'project-coverage: totalCount'")
  parser.add_argument("-tests", help="Provides the file where the test count is stored as 'total failed success'")
  parser.add_argument("-slack", help="Provides the url to report on slack")
  args = parser.parse_args()

  if not args.slack:
    sys.exit(-1)

  attachments = []
  if args.tests:
    attachments.append(buildTestsAttachment(args.tests))
  if args.coverage:
    attachments.append(buildCoverageAttachment(args.coverage))
  if args.lines:
    attachments.append(buildLinesAttachment(args.lines))

  message = {
    "text": "Android SDK Build report",
    "username": "Android SDK Reporter",
	  "attachments": attachments
  }

  postToSlack(message, args.slack)

# Utils
def getFieldResult(title, value):
  return {
    "title": title,
    "value": value,
    "short": True
  }

# Test parsing
def buildTestsAttachment(testFile):
  parsedResult = parseTestFile(testFile)
  color = {True: "danger", False: "good"}[parsedResult["failed"] > 0]
  return {
      "title": "Tests",
      "text": "Tests in sdk and libraries",
			"fields": [
        getFieldResult("Total", parsedResult["total"]),
        getFieldResult("Passed", parsedResult["passed"]),
        getFieldResult("Failed", parsedResult["failed"])
      ],
      "mrkdwn_in": ["text"],
			"color": color,
			"footer": "Bamboo tests"
  }

def parseTestFile(file):
  with open(file, "r") as openedFile:
    line = openedFile.readline().split(" ")
    return {"passed": int(line[0]), "failed": int(line[1]), "total": int(line[2])}

# Coverage
def buildCoverageAttachment(file):
  parsedResult = parseCoverageFile(file)
  fields = []
  color = "good"
  for coverageTuple in parsedResult:
    coverage = coverageTuple["coverage"]
    fields.append(getFieldResult(coverageTuple["name"], str(coverage) + "%"))
    if color == "good" and (coverage < 65 and coverage > 25):
      color = "warning"
    elif (color == "good" or color == "warning") and (coverage <= 25):
      color = "danger"

  return {
      "title": "Coverage",
      "text": "Coverage per project",
			"fields": fields,
      "mrkdwn_in": ["text"],
			"color": color,
			"footer": "Bamboo coverage"
  }

def parseCoverageFile(file):
  coverages = []
  with open(file, "r") as openedFile:
    for line in openedFile:
      tuple = line.split(":")
      coverages.append({"name": tuple[0], "coverage": int(tuple[1])})
  return coverages

def buildLinesAttachment(file):
  parsedResult = parseLinesFile(file)
  return {
    "title": "Project lines",
    "text": "Line distribution based on the project",
    "fields": [
      getFieldResult("Total", parsedResult["total"]),
      getFieldResult("Source", parsedResult["source"]),
      getFieldResult("Comments", parsedResult["comments"])
    ],
    "mrkdwn_in": ["text"],
    "footer": "Bamboo sloc"
  }

def parseLinesFile(file):
  with open(file, 'r') as openedFile:
    items = openedFile.readline().split(" ")
    return {
      "total": items[0],
      "source": items[1],
      "comments": items[2]
    }
    

# Post to slack
def postToSlack(message, slackHook):
  requests.post(slackHook, json=message)

if __name__ == "__main__":
   main()