# Depends on:
# * Python 3.x
# * PySimpleSoap library (install via pip/git)


from pysimplesoap.client import SoapClient
import datetime
import time

COMPLETED = 'COMPLETED'
RUNNING = 'RUNNING'
QUEUED = 'QUEUED'
FAILED = 'FAILED'
UNRECOGNIZED_TOKEN = 'UNRECOGNIZED_TOKEN'

WSDL = "http://localhost/whitney/Whitney_DEV/GetTableContents.lcsx?WSDL"
TRACE = False

client = SoapClient(wsdl=WSDL, trace=TRACE)

swr = client.StartWorkflow()
status = ''
token = swr['StartWorkflowResult']['token']
interval = swr['StartWorkflowResult']['interval']
ewr = client.EndWorkflow(token=token)

while 1:
    status = ewr['EndWorkflowResult']['status']
    print(datetime.datetime.today(), status)

    if status == FAILED:
        break
    if status == COMPLETED:
        break
    if status == UNRECOGNIZED_TOKEN:
        break

    time.sleep(interval)

    ewr = client.EndWorkflow(token=token)

if status == COMPLETED:
    print(datetime.datetime.today(), ewr['EndWorkflowResult']['workflowResult'])
