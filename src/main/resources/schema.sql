-- =========================================================
-- BookIT PostgreSQL schema
-- MongoDB handles authentication.
-- PostgreSQL handles the business flow.
-- =========================================================

CREATE TABLE IF NOT EXISTS department (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    mongo_user_id VARCHAR(24) UNIQUE NOT NULL,
    department_id INTEGER,
    name VARCHAR(150) NOT NULL,
    role VARCHAR(50) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(20),

    CONSTRAINT fk_user_department
        FOREIGN KEY (department_id)
        REFERENCES department(id)
);

CREATE TABLE IF NOT EXISTS building (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS floor (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS building_floor (
    building_id INTEGER NOT NULL,
    floor_id INTEGER NOT NULL,

    CONSTRAINT pk_building_floor
        PRIMARY KEY (building_id, floor_id),

    CONSTRAINT fk_building_floor_building
        FOREIGN KEY (building_id)
        REFERENCES building(id),

    CONSTRAINT fk_building_floor_floor
        FOREIGN KEY (floor_id)
        REFERENCES floor(id)
);

CREATE TABLE IF NOT EXISTS room (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    floor_id INTEGER NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    capacity INTEGER NOT NULL,

    CONSTRAINT chk_room_capacity
        CHECK (capacity > 0),

    CONSTRAINT fk_room_floor
        FOREIGN KEY (floor_id)
        REFERENCES floor(id)
);

CREATE TABLE IF NOT EXISTS seat (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    room_id INTEGER NOT NULL,
    code VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'available',
    type VARCHAR(50) NOT NULL,

    CONSTRAINT uq_seat_room_code
        UNIQUE (room_id, code),

    CONSTRAINT fk_seat_room
        FOREIGN KEY (room_id)
        REFERENCES room(id)
);

CREATE TABLE IF NOT EXISTS reservation (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id INTEGER NOT NULL,
    seat_id INTEGER,
    room_id INTEGER,
    start_date_time TIMESTAMP NOT NULL,
    end_date_time TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    recurrence INTEGER NOT NULL DEFAULT 0,
    reminder_sent BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT chk_reservation_dates
        CHECK (end_date_time > start_date_time),

    CONSTRAINT chk_reservation_recurrence
        CHECK (recurrence >= 0),

    CONSTRAINT chk_reservation_target
        CHECK (
            (seat_id IS NOT NULL AND room_id IS NULL)
            OR
            (seat_id IS NULL AND room_id IS NOT NULL)
        ),

    CONSTRAINT fk_reservation_user
        FOREIGN KEY (user_id)
        REFERENCES users(id),

    CONSTRAINT fk_reservation_seat
        FOREIGN KEY (seat_id)
        REFERENCES seat(id),

    CONSTRAINT fk_reservation_room
        FOREIGN KEY (room_id)
        REFERENCES room(id)
);

CREATE TABLE IF NOT EXISTS invitation (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sender_id INTEGER NOT NULL,
    receiver_id INTEGER NOT NULL,
    seat_id INTEGER NOT NULL,
    start_date_time TIMESTAMP NOT NULL,
    end_date_time TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_reservation_id INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    responded_at TIMESTAMP,

    CONSTRAINT chk_invitation_users
        CHECK (sender_id <> receiver_id),

    CONSTRAINT chk_invitation_dates
        CHECK (end_date_time > start_date_time),

    CONSTRAINT uq_invitation_created_reservation
        UNIQUE (created_reservation_id),

    CONSTRAINT fk_invitation_sender
        FOREIGN KEY (sender_id)
        REFERENCES users(id),

    CONSTRAINT fk_invitation_receiver
        FOREIGN KEY (receiver_id)
        REFERENCES users(id),

    CONSTRAINT fk_invitation_seat
        FOREIGN KEY (seat_id)
        REFERENCES seat(id),

    CONSTRAINT fk_invitation_created_reservation
        FOREIGN KEY (created_reservation_id)
        REFERENCES reservation(id)
);

CREATE TABLE IF NOT EXISTS notification (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id INTEGER NOT NULL,
    reservation_id INTEGER,
    invitation_id INTEGER,
    title VARCHAR(255) NOT NULL,
    message TEXT,
    type VARCHAR(50) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_notification_user
        FOREIGN KEY (user_id)
        REFERENCES users(id),

    CONSTRAINT fk_notification_reservation
        FOREIGN KEY (reservation_id)
        REFERENCES reservation(id),

    CONSTRAINT fk_notification_invitation
        FOREIGN KEY (invitation_id)
        REFERENCES invitation(id)
);

CREATE TABLE IF NOT EXISTS audit_log (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id INTEGER NOT NULL,
    reservation_id INTEGER,
    action VARCHAR(100) NOT NULL,
    source VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_audit_user
        FOREIGN KEY (user_id)
        REFERENCES users(id),

    CONSTRAINT fk_audit_reservation
        FOREIGN KEY (reservation_id)
        REFERENCES reservation(id)
);

ALTER TABLE reservation ADD COLUMN IF NOT EXISTS reminder_sent BOOLEAN NOT NULL DEFAULT FALSE;

