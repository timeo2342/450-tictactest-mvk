# DevContainer for TicTacTest
# Alpine-based image with Java 25 (Eclipse Temurin).
# Gradle is provided by the project's Gradle Wrapper (./gradlew),
# JUnit is pulled as a Gradle test dependency (see build.gradle).
FROM eclipse-temurin:25-jdk-alpine

# Basic tooling needed for development inside the container.
# - bash: nicer shell than the default ash
# - git:  version control
# - shadow/sudo: to create a non-root user and allow admin tasks
RUN apk add --no-cache bash git shadow sudo

# Create a non-root user with UID:GID 1000:1000 (as required by the assignment).
ARG USERNAME=dev
ARG USER_UID=1000
ARG USER_GID=1000
RUN addgroup -g ${USER_GID} ${USERNAME} \
	&& adduser -D -u ${USER_UID} -G ${USERNAME} -s /bin/bash ${USERNAME} \
	&& echo "${USERNAME} ALL=(ALL) NOPASSWD:ALL" > /etc/sudoers.d/${USERNAME} \
	&& chmod 0440 /etc/sudoers.d/${USERNAME}

# Work as the non-root user by default.
USER ${USERNAME}
WORKDIR /workspace

# Default shell
ENV SHELL=/bin/bash
