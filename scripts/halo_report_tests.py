#!/usr/bin/python
import sys
import os
import requests

def main(argv):
    print '--- Parsing tests to dashboard ---'
    tests = 0
    success = 0
    failures = 0
    for file in argv:
      if(os.path.exists(file)):
        os.system('sed -e "s/<[^>]*p>//g" {0} >> tests.tmp'.format(file))
        currentTests = int(os.popen('xmllint --html -xpath "//div[@id=\'tests\']/div/text()[1]" tests.tmp').read())
        currentFailures = int(os.popen('xmllint --html -xpath "//div[@id=\'failures\']/div/text()[1]" tests.tmp').read())
        os.system('rm tests.tmp')
        success = success + (currentTests - currentFailures)
        tests += currentTests
        failures += currentFailures 

    sendReport(success, failures, tests)
    print '--- PASSING: {0} FAILURES: {1} TESTS: {2} ---'.format(success, failures, tests)

def sendReport(success, failures, tests):
    # Send the report
    binary = "test_results,platform=android passed={0},failed={1},total={2}".format(success, failures, tests)
    requests.post("http://halo-dashboard.aws.mobgen.com:8086/write?db=halo", data=binary, headers={'Content-Type': 'application/octet-stream'})

    # Save the report
    filename = "../reports/tests.report"
    if not os.path.exists(filename):
        os.makedirs(os.path.dirname(filename))
    with open(filename, "w") as f:
        f.write("{0} {1} {2}".format(success, failures, tests))

if __name__ == "__main__":
   main(sys.argv[1:])