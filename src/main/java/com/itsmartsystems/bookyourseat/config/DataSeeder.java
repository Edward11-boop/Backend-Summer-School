package com.itsmartsystems.bookyourseat.config;

import com.itsmartsystems.bookyourseat.Status;

import com.itsmartsystems.bookyourseat.model.Seat;
import com.itsmartsystems.bookyourseat.model.Room;
import com.itsmartsystems.bookyourseat.repository.ReservationRepository;
import com.itsmartsystems.bookyourseat.repository.SeatRepository;
import com.itsmartsystems.bookyourseat.repository.RoomRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

        private final RoomRepository roomRepository;
        private final SeatRepository locRepository;
        private final ReservationRepository reservationRepository;

        public DataSeeder(RoomRepository roomRepository, SeatRepository locRepository,
                        ReservationRepository reservationRepository) {
                this.roomRepository = roomRepository;
                this.locRepository = locRepository;
                this.reservationRepository = reservationRepository;
        }

        @Override
        public void run(String... args) {
                if (roomRepository.count() > 0) {
                        ensureMissingSeats();
                        System.out.println("Salile exista deja, au fost verificate locurile lipsa.");
                        return;
                }

                creeazaSalaCuLocuri("SD0", "Zona Stand-Up Desks", "P", "DESK_AREA", false, 8,
                                new SeatGroup("P-SD0-", 1, 8));

                creeazaSalaCuLocuri("S0", "Room Sedinte S0", "P", "MEETING_ROOM", true, 8,
                                new SeatGroup("P-S0-", 1, 8));

                creeazaSalaCuLocuri("B0", "Room Birouri B0", "P", "OFFICE_AREA", false, 12,
                                new SeatGroup("P-B0-", 1, 12));

                creeazaSalaCuLocuri("E1", "Room Evenimente E1", "T1E1", "EVENT_ROOM", true, 29,
                                new SeatGroup("T1-SD1-", 1, 6),
                                new SeatGroup("T1-E1-", 7, 29));

                creeazaSalaCuLocuri("S1", "Room Sedinte S1", "T1E1", "MEETING_ROOM", true, 8,
                                new SeatGroup("T1-S1-", 1, 8));

                creeazaSalaCuLocuri("G2", "Room Gaming G2", "T1E2", "GAME_ROOM", false, 8,
                                new SeatGroup("T1-G2-", 1, 8));

                creeazaSalaCuLocuri("404", "Room 404", "T2E1", "MEETING_ROOM", true, 10,
                                new SeatGroup("T2-404-", 1, 10));

                creeazaSalaCuLocuri("B1", "Room Birouri B1", "T2E1", "OFFICE_AREA", false, 18,
                                new SeatGroup("T2-B1-", 1, 18));

                creeazaSalaCuLocuri("O2", "Outland O2", "T2E2", "EVENT_ROOM", true, 10,
                                new SeatGroup("T2-O2-", 1, 10));

                creeazaSalaCuLocuri("B2", "Room Birouri B2", "T2E2", "OFFICE_AREA", false, 16,
                                new SeatGroup("T2-SD2-", 1, 4),
                                new SeatGroup("T2-B2-", 5, 16));

                System.out.println("Seeder terminat - sÄli Č™i locuri populate cu succes!");
        }


        private void ensureMissingSeats() {
                roomRepository.findByCode("E1").ifPresent(room -> {
                        String code = "T1-E1-29";
                        String status = getRoomSeatStatus(room.getId());
                        Seat seat = locRepository.findByCode(code)
                                        .orElseGet(() -> new Seat(room.getId(), code, status));
                        seat.setStatus(status);
                        locRepository.save(seat);
                });
        }

        private String getRoomSeatStatus(Long roomId) {
                if (!reservationRepository.findByRoom_IdAndStatus(roomId, Status.APPROVED).isEmpty()) {
                        return "OCCUPIED";
                }

                if (!reservationRepository.findByRoom_IdAndStatus(roomId, Status.PENDING).isEmpty()) {
                        return "PENDING";
                }

                return "AVAILABLE";
        }
        private void creeazaSalaCuLocuri(String cod, String name, String level, String tipSpatiu,
                        boolean inchiriereToataSala, int capacitate, SeatGroup... grupuri) {

                Room room = new Room(cod, name, level, tipSpatiu, inchiriereToataSala, capacitate, "ACTIVE");
                room = roomRepository.save(room);

                List<Seat> locuri = new ArrayList<>();
                for (SeatGroup grup : grupuri) {
                        for (int i = grup.start; i <= grup.end; i++) {
                                String codLoc = grup.prefix + String.format("%02d", i);
                                locuri.add(new Seat(room.getId(), codLoc, "ACTIVE"));
                        }
                }
                locRepository.saveAll(locuri);
        }

        private static class SeatGroup {
                String prefix;
                int start;
                int end;

                SeatGroup(String prefix, int start, int end) {
                        this.prefix = prefix;
                        this.start = start;
                        this.end = end;
                }
        }
}

