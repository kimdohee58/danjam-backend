package com.danjam.search.querydsl;

import com.danjam.amenity.AmenityListDTO;
import com.danjam.amenity.QAmenity;
import com.danjam.booking.QBooking;
import com.danjam.d_amenity.QDamenity;
import com.danjam.d_category.QDcategory;
import com.danjam.dorm.QDorm;
import com.danjam.review.QReview;
import com.danjam.room.QRoom;
import com.danjam.room.RoomDetailDTO;
import com.danjam.roomImg.QRoomImg;
import com.danjam.search.SearchDto;
import com.danjam.users.QUsers;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.querydsl.core.types.Projections.list;
import static com.querydsl.core.types.Projections.map;

@Repository
@RequiredArgsConstructor
public class SearchRepoImpl implements SearchRepo {
    private final JPAQueryFactory queryFactory;
    private final QDorm qDorm = QDorm.dorm;
    private final QUsers qUsers = QUsers.users;
    private final QDcategory qDcategory = QDcategory.dcategory;
    private final QRoom qRoom = QRoom.room;
    private final QAmenity qAmenity = QAmenity.amenity;
    private final QDamenity qDamenity = QDamenity.damenity;
    private final QReview qReview = QReview.review;
    private final QRoomImg qRoomImg = QRoomImg.roomImg;
    private final QBooking qBooking = QBooking.booking;

    QDorm subDorm = new QDorm("subDorm");
    QRoom subRoom = new QRoom("subRoom");
    QRoomImg subImg = new QRoomImg("subImg");
    QReview subReview = new QReview("subReview");
    QBooking subBooking = new QBooking("subBooking");
    QDamenity subDamenity = new QDamenity("subDamenity");

    // Status filter to ensure only active dorms are considered
    private final BooleanExpression statusFilter = qDorm.status.eq("Y");

    // Subquery for finding the minimum room price for each dorm
    JPQLQuery<Integer> groupByDorm = JPAExpressions
            .select(subRoom.price.min())
            .from(subRoom)
            .where(subRoom.dorm.id.eq(qDorm.id))
            .groupBy(subRoom.dorm.id);

    // Subquery for calculating the average review rating for each dorm
    JPQLQuery<Double> groupByReview = JPAExpressions
            .select(subReview.rate.avg())
            .from(subReview)
            .join(subBooking).on(subReview.booking.id.eq(subBooking.id))
            .join(subRoom).on(subBooking.room.id.eq(subRoom.id))
            .where(subRoom.dorm.id.eq(qDorm.id));

    public List<DormDto> removeDorm(List<DormDto> dormDtoList) {
        for (DormDto dormDto : dormDtoList) {
            List<RoomDto> roomDtos = queryFactory
                    .select(Projections.constructor(RoomDto.class,
                            qRoom.id,
                            qRoom.person,
                            qRoom.price,
                            list(Projections.constructor(ImgDto.class,
                                    qRoomImg.id,
                                    qRoomImg.name,
                                    qRoomImg.ext))
                    ))
                    .from(qRoom)
                    .leftJoin(qRoomImg).on(qRoom.id.eq(qRoomImg.room.id))
                    .where(qRoom.dorm.id.eq(dormDto.getId()))
                    .fetch();

            for (RoomDto roomDto : roomDtos) {
                List<ImgDto> images = queryFactory
                        .select(Projections.constructor(ImgDto.class,
                                qRoomImg.id,
                                qRoomImg.name,
                                qRoomImg.ext
                        ))
                        .from(qRoomImg)
                        .where(qRoomImg.room.id.eq(roomDto.getId()))
                        .fetch();

                roomDto.setImages(images);
                dormDto.setRoom(roomDto);
            }
        }

        Map<Long, DormDto> dormDtoMap = new HashMap<>();
        for (DormDto dormDto : dormDtoList) {
            dormDtoMap.merge(dormDto.getId(), dormDto, (existingDormDto, newDormDto) -> {
                // Merge existing room and image data
                existingDormDto.setRoom(newDormDto.getRoom());
                return existingDormDto;

            });
        }

        return new ArrayList<>(dormDtoMap.values());
    }

    @Override
    public Page<DormDto> findAllList(Pageable pageable) {
        List<DormDto> dormDtoList = queryFactory
                .select(Projections.constructor(DormDto.class,
                        qDorm.id,
                        qDorm.name,
                        qDorm.description,
                        qDorm.contactNum,
                        qDorm.city,
                        qDorm.town,
                        qDorm.address,
                        Projections.constructor(CategoryDto.class,
                                qDcategory.id,
                                qDcategory.name),
                        Projections.constructor(UserDto.class,
                                qUsers.id,
                                qUsers.name,
                                qUsers.role),
                        Projections.constructor(RoomDto.class,
                                qRoom.id,
                                qRoom.person,
                                qRoom.price,
                                list(Projections.constructor(ImgDto.class,
                                        qRoomImg.id,
                                        qRoomImg.name,
                                        qRoomImg.ext))),
                        groupByReview
                ))
                .from(qDorm)
                .join(qDorm.dcategory, qDcategory)
                .join(qDorm.user, qUsers)
                .join(qRoom).on(qDorm.id.eq(qRoom.dorm.id))
                .leftJoin(qRoomImg).on(qRoom.id.eq(qRoomImg.room.id))
                .where(qRoom.price.eq(groupByDorm), statusFilter)// status = Y
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(qDorm.count())
                .from(qDorm)
                .where(statusFilter) // status = Y
                .fetchOne();

        List<DormDto> resultList = removeDorm(dormDtoList);

        int sizeDifference = pageable.getPageSize() - resultList.size();
        if (sizeDifference > 0) {
            for (int i = 0; i < sizeDifference; i++) {
                resultList.add(new DormDto());
            }
        }

        System.out.println("가져온 도미토리 수: " + dormDtoList.size());
        System.out.println("resultList = " + resultList.size());

        return new PageImpl<>(resultList, pageable, total);
    }

    @Override
    public List<String> findByCity(String city) {
        if (city == null || city.isEmpty()) {
            return null;
        }
        return queryFactory
                .select(qDorm.town)
                .from(qDorm)
                .where(qDorm.city.eq(city), statusFilter) // status = Y
                .distinct()
                .fetch();
    }

    @Override
    public List<DormDto> findList(SearchDto searchDto) {
        System.out.println("findList");
        String city = searchDto.getCity();
        LocalDateTime checkIn = searchDto.getCheckIn();
        LocalDateTime checkOut = searchDto.getCheckOut();
        int person = searchDto.getPerson();
        System.out.println(searchDto);

        // Subquery for finding the minimum room price for each dorm
        JPQLQuery<Integer> groupByDorm = JPAExpressions
                .select(subRoom.price.min())
                .from(subRoom)
                .where(subRoom.person.goe(person),
                        subRoom.dorm.id.eq(qDorm.id))
                .groupBy(subRoom.dorm.id);

        // Subquery for finding booked rooms between the given dates
        JPQLQuery<Long> groupByDate = JPAExpressions
                .select(subBooking.room.id)
                .from(subBooking)
                .where(subBooking.checkIn.between(checkIn, checkOut),
                        subBooking.checkOut.between(checkIn, checkOut));

        // Create search filter
        BooleanExpression groupBySearch;
        if (searchDto.getCity().equalsIgnoreCase("선택")) { // City not selected
            groupBySearch = qRoom.id.notIn(groupByDate)
                    .and(qRoom.price.eq(groupByDorm))
                    .and(qDorm.id.eq(qRoom.dorm.id));
        } else {
            groupBySearch = qRoom.id.notIn(groupByDate)
                    .and(qRoom.price.eq(groupByDorm))
                    .and(qDorm.city.eq(city))
                    .and(qDorm.id.eq(qRoom.dorm.id));
        }

        List<DormDto> dormDtoList = queryFactory
                .select(Projections.constructor(DormDto.class,
                        qDorm.id,
                        qDorm.name,
                        qDorm.description,
                        qDorm.contactNum,
                        qDorm.city,
                        qDorm.town,
                        qDorm.address,
                        Projections.constructor(CategoryDto.class,
                                qDcategory.id,
                                qDcategory.name),
                        Projections.constructor(UserDto.class,
                                qUsers.id,
                                qUsers.name,
                                qUsers.role),
                        Projections.constructor(RoomDto.class,
                                qRoom.id,
                                qRoom.person,
                                qRoom.price,
                                list(Projections.constructor(ImgDto.class,
                                        qRoomImg.id,
                                        qRoomImg.name,
                                        qRoomImg.ext))),
                        groupByReview
                ))
                .from(qDorm)
                .join(qDorm.dcategory, qDcategory)
                .join(qDorm.user, qUsers)
                .join(qRoom).on(qDorm.id.eq(qRoom.dorm.id))
                .leftJoin(qRoomImg).on(qRoom.id.eq(qRoomImg.room.id))
                .where(groupBySearch.and(statusFilter)) // status = Y
                .fetch();

        return removeDorm(dormDtoList);
    }

    @Override
    public List<DormDto> findByFilter(FilterDto filterDto) {
        System.out.println("findByAmenity");
        System.out.println(filterDto.getSearchDto());
        System.out.println(filterDto.getAmenities());
        System.out.println(filterDto.getCities());

        LocalDateTime checkIn = filterDto.getSearchDto().getCheckIn();
        LocalDateTime checkOut = filterDto.getSearchDto().getCheckOut();
        int person = filterDto.getSearchDto().getPerson();

        // Subquery for finding the minimum room price for each dorm
        JPQLQuery<Integer> groupByDorm = JPAExpressions
                .select(subRoom.price.min())
                .from(subRoom)
                .where(subRoom.person.goe(person),
                        subRoom.dorm.id.eq(qDorm.id))
                .groupBy(subRoom.dorm.id);

        // Subquery for finding booked rooms between the given dates
        JPQLQuery<Long> groupByDate = JPAExpressions
                .select(subBooking.room.id)
                .from(subBooking)
                .where(subBooking.checkIn.between(checkIn, checkOut),
                        subBooking.checkOut.between(checkIn, checkOut));

        // Create search filter
        BooleanExpression groupBySearch;
        if (filterDto.getSearchDto().getCity().equalsIgnoreCase("선택")) { // City not selected
            groupBySearch = qRoom.id.notIn(groupByDate)
                    .and(qRoom.price.eq(groupByDorm))
                    .and(qDorm.id.eq(qRoom.dorm.id));
        } else {
            groupBySearch = qRoom.id.notIn(groupByDate)
                    .and(qRoom.price.eq(groupByDorm))
                    .and(qDorm.city.eq(filterDto.getSearchDto().getCity()))
                    .and(qDorm.id.eq(qRoom.dorm.id));
        }

        // Filter by town and amenities
        JPQLQuery<Long> groupByTown = JPAExpressions
                .select(subDorm.id)
                .from(subDorm)
                .where(subDorm.town.in(filterDto.getCities()));

        JPQLQuery<Long> groupByDamenity = JPAExpressions
                .select(subDamenity.dorm.id)
                .from(subDamenity)
                .where(subDamenity.amenity.id.in(filterDto.getAmenities()))
                .groupBy(subDamenity.dorm.id)
                .having(subDamenity.amenity.id.countDistinct().eq((long) filterDto.getAmenities().size()));

        // Filter by amenities and town
        JPQLQuery<Long> groupByFilter;

        if (filterDto.getCities().isEmpty() && !filterDto.getAmenities().isEmpty()) {
            groupByFilter = JPAExpressions
                    .select(subDamenity.dorm.id)
                    .from(subDamenity)
                    .where(subDamenity.amenity.id.in(filterDto.getAmenities()))
                    .groupBy(subDamenity.dorm.id)
                    .having(subDamenity.amenity.id.countDistinct().eq((long) filterDto.getAmenities().size()));
        } else if (!filterDto.getCities().isEmpty() && filterDto.getAmenities().isEmpty()) {
            groupByFilter = JPAExpressions
                    .select(subDorm.id)
                    .from(subDorm)
                    .where(subDorm.town.in(filterDto.getCities()));
        } else {
            groupByFilter = JPAExpressions
                    .select(qDamenity.dorm.id)
                    .from(qDamenity)
                    .join(qDorm).on(qDamenity.dorm.id.eq(qDorm.id))
                    .where(qDorm.id.in(groupByTown).and(qDorm.id.in(groupByDamenity)));
        }

        List<DormDto> dormDtoList = queryFactory
                .select(Projections.constructor(DormDto.class,
                        qDorm.id,
                        qDorm.name,
                        qDorm.description,
                        qDorm.contactNum,
                        qDorm.city,
                        qDorm.town,
                        qDorm.address,
                        Projections.constructor(CategoryDto.class,
                                qDcategory.id,
                                qDcategory.name),
                        Projections.constructor(UserDto.class,
                                qUsers.id,
                                qUsers.name,
                                qUsers.role),
                        Projections.constructor(RoomDto.class,
                                qRoom.id,
                                qRoom.person,
                                qRoom.price,
                                list(Projections.constructor(ImgDto.class,
                                        qRoomImg.id,
                                        qRoomImg.name,
                                        qRoomImg.ext))),
                        groupByReview
                ))
                .from(qDorm)
                .join(qDorm.dcategory, qDcategory)
                .join(qDorm.user, qUsers)
                .join(qRoom).on(qDorm.id.eq(qRoom.dorm.id))
                .leftJoin(qRoomImg).on(qRoom.id.eq(qRoomImg.room.id))
                .where(groupBySearch,
                        qDorm.id.in(groupByFilter),
                        statusFilter) // status = Y
                .fetch();

        return removeDorm(dormDtoList);
    }

    @Override
    public List<RoomDetailDTO> findAllRoom(SearchDto searchDto, Long dormId) {
        System.out.println("findAllRoom");
        System.out.println(searchDto);
        System.out.println(dormId);
        LocalDateTime checkIn = searchDto.getCheckIn();
        LocalDateTime checkOut = searchDto.getCheckOut();
        int person = searchDto.getPerson();

        // Subquery for finding booked rooms between the given dates
        JPQLQuery<Long> groupByDate = JPAExpressions
                .select(subBooking.room.id)
                .from(subBooking)
                .where(subBooking.checkIn.between(checkIn, checkOut),
                        subBooking.checkOut.between(checkIn, checkOut)
                );

        // Retrieve room details excluding booked rooms
        List<RoomDetailDTO> roomDtoList = queryFactory
                .select(Projections.constructor(RoomDetailDTO.class,
                        qRoom.id,
                        qRoom.name,
                        qRoom.description,
                        qRoom.person,
                        qRoom.price,
                        qRoom.type,
                        list(Projections.constructor(ImgDto.class,
                                qRoomImg.id,
                                qRoomImg.name,
                                qRoomImg.ext))))
                .from(qRoom)
                .join(qRoom).on(qDorm.id.eq(qRoom.dorm.id))
                .leftJoin(qRoomImg).on(qRoom.id.eq(qRoomImg.room.id))
                .orderBy(qRoom.price.asc())
                .where(
                        qRoom.id.notIn(groupByDate),
                        qRoom.dorm.id.eq(dormId),
                        qRoom.person.goe(person),
                        statusFilter) // status = Y
                .fetch();
        System.out.println("findAllRoom rooms:"+roomDtoList); // Debug statement

        for (RoomDetailDTO roomDto : roomDtoList) {
            List<ImgDto> images = queryFactory
                    .select(Projections.constructor(ImgDto.class,
                            qRoomImg.id,
                            qRoomImg.name,
                            qRoomImg.ext
                    ))
                    .from(qRoomImg)
                    .where(qRoomImg.room.id.eq(roomDto.getId()))
                    .fetch();

            roomDto.setImages(images);
        }

        Map<Long, RoomDetailDTO> roomDtoMap = new HashMap<>();
        for (RoomDetailDTO roomDto : roomDtoList) {
            roomDtoMap.merge(roomDto.getId(), roomDto, (existingRoomDto, newRoomDto) -> {
                // Merge existing room and image data
                existingRoomDto.setImages(newRoomDto.getImages());
                return existingRoomDto;

            });
        }

        return new ArrayList<>(roomDtoMap.values());
    }

    @Override
    public List<AmenityListDTO> findAmenity(Long dormId) {
        return queryFactory
                .select(Projections.constructor(AmenityListDTO.class,
                        qAmenity.id,
                        qAmenity.name))
                .from(qAmenity)
                .join(qDamenity).on(qAmenity.id.eq(qDamenity.amenity.id))
                .where(qDamenity.dorm.id.eq(dormId))
                .fetch();
    }
}
