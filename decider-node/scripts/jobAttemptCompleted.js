const { pulsar } = require('../pulsar');
const { forEngineMessage } = require('../avro');

const jobId = '64aa630f-2ff6-45cc-adec-2e1cad3538f3';
const jobAttemptId = 'a216ea2c-63fb-42c8-8fe8-4a169b0b4ce8';
const jobAttemptRetry = 0;
const jobAttemptIndex = 0;

(async () => {
  // Create a producer
  const producer = await pulsar.createProducer({
    topic: 'persistent://public/default/engine',
    sendTimeoutMs: 30000,
    batchingEnabled: false,
  });

  var m = new Object()
  m.jobId = jobId
  m.sentAt = 1588705988
  m.jobAttemptId = jobAttemptId
  m.jobAttemptRetry = jobAttemptRetry
  m.jobAttemptIndex = jobAttemptIndex
  m.output = { "bytes": Buffer.from('def') }

  var msg = new Object()
  msg.type = "JobAttemptCompleted"
  msg.jobId = m.jobId
  msg[msg.type] = {"com.zenaton.jobManager.messages.AvroJobAttemptCompleted": m}

  // Send message
  producer.send({data: forEngineMessage.toBuffer(msg)});
  await producer.flush();

  await producer.close();
  await pulsar.close();
})();