package com.itsmartsystems.bookyourseat.service;

import com.itsmartsystems.bookyourseat.Status;

import com.itsmartsystems.bookyourseat.dto.ColleaguePreferenceDto;
import com.itsmartsystems.bookyourseat.dto.EmployeeAttendanceDto;
import com.itsmartsystems.bookyourseat.dto.HrReportsDto;
import com.itsmartsystems.bookyourseat.dto.MonthlyTrafficDto;
import com.itsmartsystems.bookyourseat.dto.RoomUtilizationDto;
import com.itsmartsystems.bookyourseat.dto.UserDetails;
import com.itsmartsystems.bookyourseat.dto.ZonePopularityDto;
import com.itsmartsystems.bookyourseat.model.Department;
import com.itsmartsystems.bookyourseat.model.PostgresUser;
import com.itsmartsystems.bookyourseat.model.Reservation;
import com.itsmartsystems.bookyourseat.model.Room;
import com.itsmartsystems.bookyourseat.model.User;
import com.itsmartsystems.bookyourseat.repository.DepartmentRepository;
import com.itsmartsystems.bookyourseat.repository.PostgresUserRepository;
import com.itsmartsystems.bookyourseat.repository.ReservationRepository;
import com.itsmartsystems.bookyourseat.repository.RoomRepository;
import com.itsmartsystems.bookyourseat.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
public class HrService {

    private final UserRepository userRepository;
    private final PostgresUserRepository postgresUserRepository;
    private final DepartmentRepository departmentRepository;
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    public HrService(
            UserRepository userRepository,
            PostgresUserRepository postgresUserRepository,
            DepartmentRepository departmentRepository,
            ReservationRepository reservationRepository,
            RoomRepository roomRepository
    ) {
        this.userRepository = userRepository;
        this.postgresUserRepository = postgresUserRepository;
        this.departmentRepository = departmentRepository;
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
    }

    public List<UserDetails> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toUserDetails)
                .toList();
    }

    public List<EmployeeAttendanceDto> getAttendanceHistory(String department, String date) {
        List<PostgresUser> users = postgresUserRepository.findAll();

        if (department != null && !department.isBlank()) {
            Department departmentObj = departmentRepository.findByNameIgnoreCase(department).orElse(null);
            if (departmentObj != null && departmentObj.getId() != null) {
                Integer departmentId = Math.toIntExact(departmentObj.getId());
                users = users.stream()
                        .filter(u -> u.getDepartmentId() != null && u.getDepartmentId().equals(departmentId))
                        .toList();
            }
        }

        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        LocalDate monthEnd = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        LocalDate selectedDate = null;
        if (date != null && !date.isBlank()) {
            selectedDate = LocalDate.parse(date);
        }

        List<EmployeeAttendanceDto> result = new ArrayList<>();

        for (PostgresUser user : users) {
            List<Reservation> reservations = reservationRepository.findByUser_Id(user.getId()).stream()
                    .filter(r -> r.getStatus() != null && r.getStatus() == Status.APPROVED)
                    .filter(r -> {
                        LocalDate reservationDay = r.getStartDateTime().toLocalDate();
                        return !reservationDay.isBefore(monthStart) && !reservationDay.isAfter(monthEnd);
                    })
                    .toList();

            Set<LocalDate> attendanceDates = new HashSet<>();
            for (Reservation reservation : reservations) {
                LocalDate start = reservation.getStartDateTime().toLocalDate();
                LocalDate end = reservation.getEndDateTime().toLocalDate();

                for (LocalDate day = start; !day.isAfter(end); day = day.plusDays(1)) {
                    if (!day.isBefore(monthStart) && !day.isAfter(monthEnd)) {
                        attendanceDates.add(day);
                    }
                }
            }

            if (selectedDate != null && !attendanceDates.contains(selectedDate)) {
                continue;
            }

            int totalWorkingDays = calculateBusinessDays(monthStart, monthEnd);
            int presenceDays = attendanceDates.size();
            int attendancePercent = totalWorkingDays == 0 ? 0 : (presenceDays * 100) / totalWorkingDays;

            String departmentName = "";
            if (user.getDepartmentId() != null) {
                departmentName = departmentRepository.findById(Long.valueOf(user.getDepartmentId()))
                        .map(Department::getName)
                        .orElse("");
            }

            EmployeeAttendanceDto dto = new EmployeeAttendanceDto();
            dto.setId(String.valueOf(user.getId()));
            dto.setName(user.getName());
            dto.setDepartment(departmentName);
            dto.setPresenceDays(presenceDays);
            dto.setTotalWorkingDays(totalWorkingDays);
            dto.setAttendanceDates(attendanceDates.stream()
                    .map(LocalDate::toString)
                    .sorted()
                    .toList());

            result.add(dto);
        }

        return result;
    }

    public HrReportsDto getReports() {
        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        LocalDate monthEnd = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        List<Reservation> approvedReservations = reservationRepository.findByStatus(Status.APPROVED).stream()
                .filter(r -> r.getStartDateTime() != null)
                .filter(r -> {
                    LocalDate d = r.getStartDateTime().toLocalDate();
                    return !d.isBefore(monthStart) && !d.isAfter(monthEnd);
                })
                .toList();

        Map<DayOfWeek, Set<Integer>> dailyUsers = new EnumMap<>(DayOfWeek.class);
        for (DayOfWeek day : DayOfWeek.values()) {
            dailyUsers.put(day, new HashSet<>());
        }

        for (Reservation reservation : approvedReservations) {
            if (reservation.getUser() != null) {
                DayOfWeek day = reservation.getStartDateTime().getDayOfWeek();
                dailyUsers.get(day).add(reservation.getUser().getId());
            }
        }

        List<MonthlyTrafficDto> monthlyTraffic = List.of(
                new MonthlyTrafficDto("Luni", dailyUsers.get(DayOfWeek.MONDAY).size()),
                new MonthlyTrafficDto("Marți", dailyUsers.get(DayOfWeek.TUESDAY).size()),
                new MonthlyTrafficDto("Miercuri", dailyUsers.get(DayOfWeek.WEDNESDAY).size()),
                new MonthlyTrafficDto("Joi", dailyUsers.get(DayOfWeek.THURSDAY).size()),
                new MonthlyTrafficDto("Vineri", dailyUsers.get(DayOfWeek.FRIDAY).size())
        );

        List<RoomUtilizationDto> roomUtilization = new ArrayList<>();
        for (Room room : roomRepository.findAll()) {
            long bookings = approvedReservations.stream()
                    .map(this::resolveRoom)
                    .filter(Objects::nonNull)
                    .filter(r -> Objects.equals(r.getId(), room.getId()))
                    .count();

            long capacity = room.getCapacity() == null ? 1 : room.getCapacity();
            int utilization = capacity <= 0 ? 0 : (int) Math.min(100, (bookings * 100) / capacity);

            RoomUtilizationDto dto = new RoomUtilizationDto();
            dto.setName(room.getName());
            dto.setType(room.getType());
            dto.setUtilization(utilization + "%");
            roomUtilization.add(dto);
        }

        Map<String, Integer> zoneBookings = new HashMap<>();
        Map<String, Integer> zoneCapacity = new HashMap<>();

        for (Reservation reservation : approvedReservations) {
            Room room = resolveRoom(reservation);
            if (room == null) {
                continue;
            }

            String zone = inferZone(room.getName());
            zoneBookings.merge(zone, 1, Integer::sum);

            long capacity = room.getCapacity() == null ? 12 : room.getCapacity();
            zoneCapacity.merge(zone, (int) capacity, Integer::sum);
        }

        List<ZonePopularityDto> zonePopularity = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : zoneBookings.entrySet()) {
            String zone = entry.getKey();
            int bookings = entry.getValue();
            int capacity = zoneCapacity.getOrDefault(zone, 1);
            int occupancy = capacity == 0 ? 0 : (bookings * 100) / capacity;

            ZonePopularityDto dto = new ZonePopularityDto();
            dto.setZone(zone);
            dto.setOccupancyRate(occupancy + "%");
            dto.setLevel(levelFromPercent(occupancy));
            zonePopularity.add(dto);
        }

        HrReportsDto dto = new HrReportsDto();
        dto.setMonthlyTraffic(monthlyTraffic);
        dto.setZonePopularity(zonePopularity);
        dto.setRoomsUtilization(roomUtilization);

        return dto;
    }

    public List<ColleaguePreferenceDto> getPreferences(String name) {
        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        LocalDate monthEnd = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        List<PostgresUser> users = postgresUserRepository.findAll();

        List<ColleaguePreferenceDto> result = new ArrayList<>();

        for (PostgresUser user : users) {
            if (name != null && !name.isBlank()
                    && !user.getName().toLowerCase().contains(name.toLowerCase())) {
                continue;
            }

            List<Reservation> reservations = reservationRepository.findByUser_Id(user.getId()).stream()
                    .filter(r -> r.getStatus() != null && r.getStatus() == Status.APPROVED)
                    .filter(r -> {
                        LocalDate date = r.getStartDateTime().toLocalDate();
                        return !date.isBefore(monthStart) && !date.isAfter(monthEnd);
                    })
                    .toList();

            Set<LocalDate> attendanceDays = new HashSet<>();
            Map<String, Integer> roomCount = new HashMap<>();
            Map<String, Integer> seatCount = new HashMap<>();

            for (Reservation reservation : reservations) {
                LocalDate start = reservation.getStartDateTime().toLocalDate();
                LocalDate end = reservation.getEndDateTime().toLocalDate();

                for (LocalDate day = start; !day.isAfter(end); day = day.plusDays(1)) {
                    if (!day.isBefore(monthStart) && !day.isAfter(monthEnd)) {
                        attendanceDays.add(day);
                    }
                }

                Room room = resolveRoom(reservation);
                if (room != null) {
                    roomCount.merge(room.getName(), 1, Integer::sum);
                }

                if (reservation.getSeat() != null && reservation.getSeat().getCode() != null) {
                    seatCount.merge(reservation.getSeat().getCode(), 1, Integer::sum);
                }
            }

            int totalWorkingDays = calculateBusinessDays(monthStart, monthEnd);
            int daysPresent = attendanceDays.size();
            int presencePercent = totalWorkingDays == 0 ? 0 : (daysPresent * 100) / totalWorkingDays;

            String favoriteRoom = roomCount.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("N/A");

            String favoriteSeat = seatCount.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("N/A");

            ColleaguePreferenceDto dto = new ColleaguePreferenceDto();
            dto.setId(String.valueOf(user.getId()));
            dto.setName(user.getName());
            dto.setPresencePercent(presencePercent);
            dto.setDaysPresent(daysPresent);
            dto.setFavoriteRoom(favoriteRoom);
            dto.setFavoriteSeat(favoriteSeat);

            result.add(dto);
        }

        return result;
    }

    private UserDetails toUserDetails(User user) {
        Integer postgresUserId = postgresUserRepository.findByEmail(user.getEmail())
                .map(PostgresUser::getId)
                .orElse(null);

        return new UserDetails(
                user.getId(),
                postgresUserId,
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }

    private int calculateBusinessDays(LocalDate start, LocalDate end) {
        int count = 0;
        for (LocalDate day = start; !day.isAfter(end); day = day.plusDays(1)) {
            DayOfWeek weekday = day.getDayOfWeek();
            if (weekday != DayOfWeek.SATURDAY && weekday != DayOfWeek.SUNDAY) {
                count++;
            }
        }
        return count;
    }

    private Room resolveRoom(Reservation reservation) {
        if (reservation.getRoom() != null) {
            return reservation.getRoom();
        }

        if (reservation.getSeat() != null && reservation.getSeat().getRoom() != null) {
            return reservation.getSeat().getRoom();
        }

        return null;
    }

    private String inferZone(String roomName) {
        if (roomName == null) {
            return "Unknown";
        }

        String name = roomName.toLowerCase();
        if (name.contains("sd0") || name.contains("s0") || name.contains("b0")) return "Parter";
        if (name.contains("s1") || name.contains("e1")) return "T1 Etaj 1";
        if (name.contains("g2") || name.contains("gaming")) return "T1 Etaj 2";
        if (name.contains("404") || name.contains("b1")) return "T2 Etaj 1";
        if (name.contains("o2") || name.contains("sd2") || name.contains("b2")) return "T2 Etaj 2";
        return "General";
    }

    private String levelFromPercent(int percentage) {
        if (percentage >= 85) return "Foarte Ridicat";
        if (percentage >= 70) return "Ridicat";
        if (percentage >= 50) return "Moderat";
        return "Scăzut";
    }
}