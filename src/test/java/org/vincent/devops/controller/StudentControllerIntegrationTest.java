package org.vincent.devops.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.vincent.devops.dto.StudentDTO;
import org.vincent.devops.system.handling.dto.ErrorDTO;
import org.vincent.devops.system.handling.exceptions.StudentDuplicateException;
import org.vincent.devops.system.handling.exceptions.StudentNotFoundException;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@DatabaseSetup("classpath:StudentControllerIntegrationTest.data.xml")
@DatabaseTearDown
public class StudentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final static int TEST_DB_INIT_COUNT = 8;

    private final String controller_url_base = "/student";

    private final String URL_GET_ALL_STUDENTS = controller_url_base;
    private final String URL_CREATE_STUDENTS  = controller_url_base;
    private final String URL_UPDATE_STUDENTS  = controller_url_base;

    @Test
    public void testGetAllStudents() throws Exception {
        List<StudentDTO> dtoList = getAllStudents();
        Assert.assertThat(dtoList, Matchers.hasSize(TEST_DB_INIT_COUNT));
    }

    @Test
    public void testGetStudentByID() throws Exception {
        List<StudentDTO> dtoList = getAllStudents();
        for (StudentDTO dto : dtoList) {
            StudentDTO studentDTO = getSingleStudent(dto.getId());
            Assert.assertThat(studentDTO, Matchers.notNullValue());
        }
    }

    @Test
    public void testGetStudentByName() throws Exception {
        List<StudentDTO> dtoList = getAllStudents();
        for (StudentDTO dto : dtoList) {
            StudentDTO studentDTO = getSingleStudent(dto.getName());
            Assert.assertThat(studentDTO, Matchers.notNullValue());
        }
    }

    @Test
    public void testGetStudentByID_expectedNotFound() throws Exception {
        String url = controller_url_base + "/id/-1";

        MvcResult mvcResult = this.mockMvc.perform(get(url))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        ErrorDTO errorDTO = new Gson().fromJson(mvcResult.getResponse().getContentAsString(), ErrorDTO.class);
        Assert.assertThat(errorDTO, Matchers.notNullValue());
        Assert.assertThat(errorDTO.getHttpCode(), Matchers.is(HttpStatus.NOT_FOUND.value()));
        Assert.assertThat(errorDTO.getMessage(), Matchers.containsString("-1"));
    }

    @Test
    public void testGetStudentByName_expectedNotFound() throws Exception {
        String name = "a_not_exist_name";
        String url = controller_url_base + "/name/" + name;

        MvcResult mvcResult = this.mockMvc.perform(get(url))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        ErrorDTO errorDTO = new Gson().fromJson(mvcResult.getResponse().getContentAsString(), ErrorDTO.class);
        Assert.assertThat(errorDTO, Matchers.notNullValue());
        Assert.assertThat(errorDTO.getHttpCode(), Matchers.is(HttpStatus.NOT_FOUND.value()));
        Assert.assertThat(errorDTO.getMessage(), Matchers.containsString(name));
    }

    @Test
    public void testAddStudentWithDifferentName_expectCreated() throws Exception {
        String name = "testAddStudentWithDifferentName_expectCreated";
        StudentDTO studentDTO = newStudentDTO(name, "address");
        MvcResult mvcResult = this.mockMvc.perform(post(URL_CREATE_STUDENTS).contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(studentDtoToString(studentDTO)))
                .andExpect(status().isCreated())
                .andReturn();
        StudentDTO returnDTO = new Gson().fromJson(mvcResult.getResponse().getContentAsString(), StudentDTO.class);
        Assert.assertThat(returnDTO.getName(), Matchers.equalTo(studentDTO.getName()));
        Assert.assertThat(returnDTO.getAddress(), Matchers.equalTo(studentDTO.getAddress()));

        List<StudentDTO> dtoList = getAllStudents();
        Assert.assertThat(dtoList, Matchers.hasSize(TEST_DB_INIT_COUNT + 1));
    }

    @Test
    public void testAddStudentWithSameName_ExpectStudentDuplicateException() throws Exception {
        List<StudentDTO> dtoList = getAllStudents();
        String name = dtoList.get(0).getName();

        StudentDTO studentDTO = newStudentDTO(name, "one address");
        MvcResult mvcResult = this.mockMvc.perform(post(URL_CREATE_STUDENTS).contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(studentDtoToString(studentDTO)))
                .andExpect(status().isConflict())
                .andReturn();
        Assert.assertThat(mvcResult.getResolvedException(), IsInstanceOf.instanceOf(StudentDuplicateException.class));

        ErrorDTO errorDTO = new Gson().fromJson(mvcResult.getResponse().getContentAsString(), ErrorDTO.class);
        Assert.assertThat(errorDTO, Matchers.notNullValue());
        Assert.assertThat(errorDTO.getHttpCode(), Matchers.is(HttpStatus.CONFLICT.value()));
        Assert.assertThat(errorDTO.getMessage(), Matchers.containsString(name));
    }

    @Test
    public void testUpdateStudent() throws Exception {
        List<StudentDTO> dtoList = getAllStudents();
        StudentDTO targetStudent = dtoList.get(5);

        int id = targetStudent.getId();
        String name = "updatedName";
        String address = "updatedAddress";
        targetStudent.setName(name);
        targetStudent.setAddress(address);

        this.mockMvc.perform(put(URL_UPDATE_STUDENTS).contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(studentDtoToString(targetStudent)))
                .andExpect(status().isNoContent());

        StudentDTO insertedDTO = getSingleStudent(id);
        Assert.assertThat(insertedDTO, Matchers.equalTo(targetStudent));
    }

    @Test
    public void testUpdateStudent_expecteDuplicate() throws Exception {
        List<StudentDTO> dtoList = getAllStudents();
        StudentDTO targetStudent = dtoList.get(5);

        String name = dtoList.get(4).getName();
        String address = "updatedAddress";
        targetStudent.setName(name);
        targetStudent.setAddress(address);

        MvcResult mvcResult = this.mockMvc.perform(put(URL_UPDATE_STUDENTS).contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(studentDtoToString(targetStudent)))
                .andExpect(status().isConflict())
                .andReturn();

        Assert.assertThat(mvcResult.getResolvedException(), IsInstanceOf.instanceOf(DataIntegrityViolationException.class));
    }

    @Test
    public void testUpdateStudentWithTheSameInstance() throws Exception {
        List<StudentDTO> dtoList = getAllStudents();
        for(StudentDTO studentDTO : dtoList) {
            this.mockMvc.perform(put(URL_UPDATE_STUDENTS).contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(studentDtoToString(studentDTO)))
                    .andExpect(status().isNoContent());

            StudentDTO updatedStudent = getSingleStudent(studentDTO.getId());
            Assert.assertThat(studentDTO, Matchers.equalTo(updatedStudent));
        }
    }

    @Test
    public void testDeleteStudent() throws Exception {
        List<StudentDTO> dtoList = getAllStudents();
        for(StudentDTO studentDTO : dtoList) {
            this.mockMvc.perform(delete(controller_url_base + "/id/" + studentDTO.getId()))
                    .andExpect(status().isOk());
        }
        dtoList = getAllStudents();
        Assert.assertThat(dtoList, Matchers.empty());
    }

    @Test
    public void testDeleteNonExistStudent_expectedNotFound() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(delete(controller_url_base + "/id/-1"))
                .andExpect(status().isNotFound()).andReturn();
        Assert.assertThat(mvcResult.getResolvedException(), IsInstanceOf.instanceOf(StudentNotFoundException.class));
    }

    private String studentDtoToString(StudentDTO studentDTO) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        return objectWriter.writeValueAsString(studentDTO);
    }

    private StudentDTO newStudentDTO(String name, String address) {
        StudentDTO dto = new StudentDTO();
        dto.setAddress(address);
        dto.setName(name);
        return dto;
    }

    private StudentDTO getSingleStudent(int id) throws Exception {
        String url = controller_url_base + "/id/" + id;
        return getSingleStudentWithURL(url);
    }

    private StudentDTO getSingleStudent(String name) throws Exception {
        String url = controller_url_base + "/name/" + name;
        return getSingleStudentWithURL(url);
    }

    private StudentDTO getSingleStudentWithURL(String url) throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        String content = response.getContentAsString();

        return new Gson().fromJson(content, StudentDTO.class);
    }

    private List<StudentDTO> getAllStudents() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(URL_GET_ALL_STUDENTS))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        String content = response.getContentAsString();

        TypeToken<List<StudentDTO>> token = new TypeToken<List<StudentDTO>>() {};
        return new Gson().fromJson(content, token.getType());
    }



}
