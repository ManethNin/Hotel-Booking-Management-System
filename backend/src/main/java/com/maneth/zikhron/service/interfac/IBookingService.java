package com.maneth.zikhron.service.interfac;

import com.maneth.zikhron.dto.Response;
import com.maneth.zikhron.entity.Booking;

public interface IBookingService {

    Response saveBooking(Long roomId, Long userId, Booking bookingRequest);

    Response findBookingByConfirmationCode(String confirmationCode);

    Response getAllBookings();

    Response cancelBooking(Long bookingId);

}