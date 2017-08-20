import logging

# Kenny loggins
log = logging.getLogger(__name__)
log.setLevel(logging.WARN)
log_formatter = logging.Formatter('%(asctime)s {%(levelname)s}: %(message)s')

# console log
stream_handler = logging.StreamHandler()
stream_handler.setLevel(logging.WARN)
stream_handler.setFormatter(log_formatter)

# set it all up
log.addHandler(stream_handler)
