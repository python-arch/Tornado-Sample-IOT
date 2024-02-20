## Introduction 
This Repo aims to present the final Mobile application demo used for the proof-of-concept prototype for The IOT project
# Introducing the Middleware Architecture: Utilizing APIs

The middleware architecture is a design pattern that focuses on enhancing the functionality and flexibility of software systems by introducing a layer of middleware between the application and the underlying services. Middleware acts as a bridge, enabling communication and interaction between different components of a system. In our final solution, we have used this architecture to simplify and obtain reliable connection between the software service and the Azure IoT hub.

## API Layer

We have introduced this middleware through building an API developed using Flask to manage the connection between the Azure IoT hub and the software service.

The API layer serves as the interface between the application and the Azure IoT Hub. It provides high-level functions and methods that developers can use to send and receive messages, manage devices, and handle other IoT-related tasks. By encapsulating the IoT Hub's functionality behind an API, developers can interact with the IoT Hub using familiar programming paradigms, such as RESTful APIs.

## Building the Demo

The Demo was built using the discussed architecture, using Kotlin for native Android development and Android Studio as a development environment to ensure a reliable and fast solution natively developed for Android.

### Architecture Diagram

![Architecture Diagram](app_arch.png)

## Summary

In summary, we successfully implemented an end-to-end reliable solution. This involved simulating the entire process, starting from transmitting telemetry data from the device to the cloud. We then streamed this data to a software service (mobile application). On the other end, we enabled the mobile application to send messages back to the device through an architecture that was easy to use, reliable, and remarkably simple.

- [Github link for further info](https://github.com/python-arch)
