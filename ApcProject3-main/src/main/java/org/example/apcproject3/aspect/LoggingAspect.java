package org.example.apcproject3.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    // Pointcut for all controller methods
    @Pointcut("execution(* org.example.apcproject3.controller.*.*(..))")
    public void controllerMethods() {}

    // Pointcut for all service methods
    @Pointcut("execution(* org.example.apcproject3.service.*.*(..))")
    public void serviceMethods() {}

    // Log method entry and exit
    @Around("controllerMethods() || serviceMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        logger.info("==> {}::{} - Started with args: {}",
                   className, methodName, Arrays.toString(joinPoint.getArgs()));

        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;

            logger.info("<== {}::{} - Completed in {}ms",
                       className, methodName, duration);

            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("!!! {}::{} - Failed after {}ms with error: {}",
                        className, methodName, duration, e.getMessage());
            throw e;
        }
    }

    // Log all exceptions in service layer
    @AfterThrowing(pointcut = "serviceMethods()", throwing = "exception")
    public void logException(JoinPoint joinPoint, Exception exception) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        logger.error("Exception in {}::{} - {}: {}",
                    className, methodName, exception.getClass().getSimpleName(),
                    exception.getMessage(), exception);
    }

    // Monitor booking operations specifically
    @Before("execution(* org.example.apcproject3.service.BookingService.createBooking(..))")
    public void beforeBookingCreation(JoinPoint joinPoint) {
        logger.info("📅 New booking creation attempt - Args: {}",
                   Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "execution(* org.example.apcproject3.service.BookingService.createBooking(..))",
                   returning = "booking")
    public void afterBookingCreation(Object booking) {
        logger.info("✅ Booking created successfully: {}", booking);
    }
}
