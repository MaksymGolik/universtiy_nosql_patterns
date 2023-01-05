package com.nosqlcourse;

import com.nosqlcourse.dao.DAOFactory;
import com.nosqlcourse.dao.TypeDAO;
import com.nosqlcourse.exception.DataNotFoundException;
import com.nosqlcourse.model.RoomInfo;
import com.nosqlcourse.model.memento.RoomInfoEditor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NoSqlCourseApplicationTests {



	private final DAOFactory dao = DAOFactory.getDAOInstance(TypeDAO.MONGO);

	@Test
	void mementoTest() throws DataNotFoundException {
		RoomInfo roomInfo = dao.getHotelRoomDAO()
				.getAllRooms().stream().findFirst().get().getInfo();
		RoomInfo expected = roomInfo.clone();
		RoomInfoEditor editor = new RoomInfoEditor(roomInfo);
		editor.setCapacity(5);
		editor.setPrice(145.0);
		editor.undo();
		editor.undo();
		Assertions.assertEquals(expected,roomInfo);
	}

}
