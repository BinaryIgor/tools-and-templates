FROM python:3.9

COPY src/docker_metrics_collector.py /docker_metrics_collector.py
COPY requirements.txt requirements.txt

RUN pip install -r requirements.txt

ENTRYPOINT ["python3", "-u", "docker_metrics_collector.py"]