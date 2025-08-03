import axios from "axios"

const apiUrl = process.env.REACT_APP_BACKEND_API_URL;



export default class ApiService {


    static getHeader() {
        const token = localStorage.getItem("token");
        return {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json"
        };
    }

    /**AUTH */

    /* This  register a new user */
    static async registerUser(registration) {
        const response = await axios.post(`${apiUrl}/auth/register`, registration)
        return response.data
    }

    /* This  login a registered user */
    static async loginUser(loginDetails) {
        const response = await axios.post(`${apiUrl}/auth/login`, loginDetails)
        return response.data
    }

    /***USERS */


    /*  This is  to get the user profile */
    static async getAllUsers() {
        const response = await axios.get(`${apiUrl}/users/all`, {
            headers: this.getHeader()
        })
        return response.data
    }

    static async getUserProfile() {
        const response = await axios.get(`${apiUrl}/users/get-logged-in-profile-info`, {
            headers: this.getHeader()
        })
        return response.data
    }


    /* This is the  to get a single user */
    static async getUser(userId) {
        const response = await axios.get(`${apiUrl}/users/get-by-id/${userId}`, {
            headers: this.getHeader()
        })
        return response.data
    }

    /* This is the  to get user bookings by the user id */
    static async getUserBookings(userId) {
        const response = await axios.get(`${apiUrl}/users/get-user-bookings/${userId}`, {
            headers: this.getHeader()
        })
        return response.data
    }


    /* This is to delete a user */
    static async deleteUser(userId) {
        const response = await axios.delete(`${apiUrl}/users/delete/${userId}`, {
            headers: this.getHeader()
        })
        return response.data
    }

    /**ROOM */
    /* This  adds a new room room to the database */
    static async addRoom(formData) {
        const result = await axios.post(`${apiUrl}/rooms/add`, formData, {
            headers: {
                ...this.getHeader(),
                'Content-Type': 'multipart/form-data'
            }
        });
        return result.data;
    }

    /* This  gets all availavle rooms */
    static async getAllAvailableRooms() {
        const result = await axios.get(`${apiUrl}/rooms/all-available-rooms`)
        return result.data
    }


    /* This  gets all availavle by dates rooms from the database with a given date and a room type */
    static async getAvailableRoomsByDateAndType(checkInDate, checkOutDate, roomType) {
        const result = await axios.get(
            `${apiUrl}/rooms/available-rooms-by-date-and-type?checkInDate=${checkInDate}
		&checkOutDate=${checkOutDate}&roomType=${roomType}`
        )
        return result.data
    }

    /* This  gets all room types from thee database */
    static async getRoomTypes() {
        const response = await axios.get(`${apiUrl}/rooms/types`)
        return response.data
    }
    /* This  gets all rooms from the database */
    static async getAllRooms() {
        const result = await axios.get(`${apiUrl}/rooms/all`)
        return result.data
    }
    /* This funcction gets a room by the id */
    static async getRoomById(roomId) {
        const result = await axios.get(`${apiUrl}/rooms/room-by-id/${roomId}`)
        return result.data
    }

    /* This  deletes a room by the Id */
    static async deleteRoom(roomId) {
        const result = await axios.delete(`${apiUrl}/rooms/delete/${roomId}`, {
            headers: this.getHeader()
        })
        return result.data
    }

    /* This updates a room */
    static async updateRoom(roomId, formData) {
        const result = await axios.put(`${apiUrl}/rooms/update/${roomId}`, formData, {
            headers: {
                ...this.getHeader(),
                'Content-Type': 'multipart/form-data'
            }
        });
        return result.data;
    }


    /**BOOKING */
    /* This  saves a new booking to the databse */
    static async bookRoom(roomId, userId, booking) {

        console.log("USER ID IS: " + userId)

        const response = await axios.post(`${apiUrl}/bookings/book-room/${roomId}/${userId}`, booking, {
            headers: this.getHeader()
        })
        return response.data
    }

    /* This  gets alll bokings from the database */
    static async getAllBookings() {
        const result = await axios.get(`${apiUrl}/bookings/all`, {
            headers: this.getHeader()
        })
        return result.data
    }

    /* This  get booking by the cnfirmation code */
    static async getBookingByConfirmationCode(bookingCode) {
        const result = await axios.get(`${apiUrl}/bookings/get-by-confirmation-code/${bookingCode}`)
        return result.data
    }

    /* This is the  to cancel user booking */
    static async cancelBooking(bookingId) {
        const result = await axios.delete(`${apiUrl}/bookings/cancel/${bookingId}`, {
            headers: this.getHeader()
        })
        return result.data
    }


    /**AUTHENTICATION CHECKER */
    static logout() {
        localStorage.removeItem('token')
        localStorage.removeItem('role')
    }

    static isAuthenticated() {
        const token = localStorage.getItem('token')
        return !!token
    }

    static isAdmin() {
        const role = localStorage.getItem('role')
        return role === 'ADMIN'
    }

    static isUser() {
        const role = localStorage.getItem('role')
        return role === 'USER'
    }
}
// export default new ApiService();
