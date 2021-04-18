'use strict';

let dataGlobal = [];
let gradeStudentId;
function DataController(data) {

    this.students = ko.observableArray('');
    this.courses = ko.observableArray('');
    this.grades = ko.observableArray('');
    this.currentStudent = ko.observable('');
    this.currentStudentObj = ko.observable('');
    this.index = ko.observable('');

    const self = this;

    this.newStudent = {
        index: ko.observable(''),
        firstName: ko.observable(''),
        lastName: ko.observable(''),
        birthday: ko.observable(''),
    };
    
    this.newCourse = {
        name: ko.observable(''),
        lecturer: ko.observable(''),
    };

    this.newGrade = {
        value: ko.observable(''),
        course: ko.observable(''),
        date: ko.observable(''),
    };

    this.studentsQuery = {
        index: ko.observable(''),
        firstName: ko.observable(''),
        lastName: ko.observable(''),
        birthday: ko.observable('')
    };

    this.courseQuery = {
        name: ko.observable(''),
        lecturer: ko.observable(''),
    };

    this.gradeQuery = {
        value: ko.observable(''),
        course: ko.observable(''),
        name: ko.observable(''),
        date: ko.observable(''),
    };

    Object.keys(this.studentsQuery).forEach(function(key) {
        self.studentsQuery[key].subscribe(function() {
            self.showStudents();
        });
    });
    Object.keys(this.courseQuery).forEach(function(key) {
        self.courseQuery[key].subscribe(function() {
            self.showCourses();
        });
    });
    Object.keys(this.gradeQuery).forEach(function(key) {
        self.gradeQuery[key].subscribe(function() {
            self.showGrades();
        });
    });

    this.showStudents = function() {
        let self = this;
        console.log('http://localhost:8000/students?' + $.param(ko.mapping.toJS(self.studentsQuery)));
        $.ajax({
            type: 'GET',
            url: 'http://localhost:8000/students?' + $.param(ko.mapping.toJS(self.studentsQuery)),
            contentType: "application/json",
            dataType: "json",
            success: function(data) {
                ko.mapping.fromJS(data, {}, self.students);
                ko.computed(function() {
                    return ko.mapping.toJSON(self);
                }).subscribe(function(newValue) {
                    let stude =  JSON.parse(newValue)['students']
                    stude.forEach(function (record) {
                        self.putOnServer('students/' + record['index'], record)
                    });
                });
            },
            error:function(jq, st, error){
                console.log(error);
            }
        });
    };

    this.showCourses = function() {
        let self = this;
        $.ajax({
            type: 'GET',
            url: 'http://localhost:8000/courses?' + $.param(ko.mapping.toJS(self.courseQuery)),
            contentType: "application/json",
            dataType: "json",
            success: function(data) {
                let co = JSON.stringify(data)
                this.courses = JSON.parse(co)
                ko.mapping.fromJS(data, {}, self.courses);
                ko.computed(function() {
                    return ko.mapping.toJSON(self);
                }).subscribe(function(newValue) {
                    let stude =  JSON.parse(newValue)['courses']
                    stude.forEach(function (record) {
                        console.log(record)
                        self.putOnServer('courses/' + record['id'], record)
                    });
                });
            },
            error:function(jq, st, error){
                console.log(error);
            }
        });
    };

    this.showStudents();
    this.showCourses();

    this.showGrades = function(index) {
        let self = this;
        let studentId = ko.mapping.toJSON(this.currentStudent);
        if (index != undefined){
            window.open('#grades_form', '_self');
            this.currentStudent = JSON.parse(ko.mapping.toJSON(index))['index'];
            studentId = JSON.parse(ko.mapping.toJSON(index))['index'];
            ko.mapping.fromJS(JSON.parse(ko.mapping.toJSON(index))['index'], {}, self.index);
            ko.computed(function() {
                return ko.mapping.toJSON(self);
            })
        }
        $.ajax({
            type: 'GET',
            url: 'http://localhost:8000/students/' + studentId + '/grades' + '?' + $.param(ko.mapping.toJS(self.gradeQuery)),
            contentType: "application/json",
            dataType: "json",
            success: function(data) {
                console.log(data);
                console.log('*****************************')
                gradeStudentId = data['id']
                self.subscribeGrade(data, self);
            },
            error:function(jq, st, error){
                console.log(error);
            }
        });

    }.bind(this);

    this.deleteStudent = function(student, url) {
        let self = this
        $.ajax({
            type: 'DELETE',
            url: 'http://localhost:8000/students' + '/' + JSON.parse(ko.mapping.toJSON(student))['index'],
            contentType: "application/json",
            dataType: "json",
            success: function(data) {
                location.reload();
            },
            error:function(jq, st, error){
                console.log(error);
                location.reload();
            }
        });
    }.bind(this)

    this.deleteCourse = function(url) {
        let self = this;
        $.ajax({
            type: 'DELETE',
            url: 'http://localhost:8000/courses' + '/' + JSON.parse(ko.mapping.toJSON( url['id'])),
            contentType: "application/json",
            dataType: "json",
            success: function(data) {
                location.reload();
            },
            error:function(jq, st, error){
                console.log(error);
                location.reload();
            }
        });
    }.bind(this)

    this.deleteGrade = function(url) {
        let self = this;
        $.ajax({
            type: 'DELETE',
            url: 'http://localhost:8000/students' + '/' + self.currentStudent + '/grades/' + JSON.parse(ko.mapping.toJSON(url['id'])),
            contentType: "application/json",
            dataType: "json",
            success: function(data) {
                self.getGradesAfterAction(data);
            },
            error:function(jq, st, error){
                console.log(error);
            }
        });
    }.bind(this)

    this.addStudent = function() {
        let self = this;
        $.ajax({
            type: 'POST',
            url: 'http://localhost:8000/students',
            contentType: "application/json",
            dataType: "json",
            data: ko.mapping.toJSON(this.newStudent),
            success: function(data) {
                self.students.push(ko.mapping.fromJS(data))
                
            },
            error:function(jq, st, error){
                console.log(error);
            }
        });
        //document.forms["students_form"].reset();
    }.bind(this)

    this.addCourse = function() {
        let self = this
        $.ajax({
            type: 'POST',
            url: 'http://localhost:8000/courses',
            contentType: "application/json",
            dataType: "json",
            data: ko.mapping.toJSON(this.newCourse),
            success: function(data) {
                self.courses.push(self.newCourse)
                location.reload();
            },
            error:function(jq, st, error){
                console.log(error);
            }
        });
    }.bind(this)

    this.addGrade = function() {
        let self = this;
        $.ajax({
            type: 'POST',
            url: 'http://localhost:8000/students/' + this.currentStudent + '/grades',
            contentType: "application/json",
            dataType: "json",
            data: ko.mapping.toJSON(this.newGrade),
            success: function(data) {
                self.getGradesAfterAction(data);
                document.forms["grades_form"].reset();
            },
            error:function(jq, st, error){

            }
        });
    }.bind(this);

    this.getGradesAfterAction = function(data) {
        $.ajax({
            type: 'GET',
            url: 'http://localhost:8000/students/' + self.currentStudent + '/grades',
            contentType: "application/json",
            dataType: "json",
            success: function(data) {
                console.log(data);
                gradeStudentId = data['id']
                window.open('#grades_form', '_self');
                self.subscribeGrade(data, self);
            },
            error:function(jq, st, error){
                console.log(error);
            }
        });
    }

    this.subscribeGrade = function(data, self) {
        ko.mapping.fromJS(data, {}, self.grades);
        ko.computed(function() {
            return ko.mapping.toJSON(self);
        }).subscribe(function(newValue) {
            let stude =  JSON.parse(newValue)['grades']
            stude.forEach(function (record) {
                console.log(record)
                let body = {"value": record["value"].toString(), "course": record["course"], "date": record['date']}
                console.log(JSON.stringify(body));
                self.putOnServer('students/' + self.currentStudent + '/grades/' + record['id'], body)
            });
        });
    }

    this.putOnServer = function(url, data) {
        $.ajax({
            type: 'PUT',
            url: 'http://localhost:8000/' + url,
            contentType: "application/json",
            accept: "application/json",
            dataType: "json",
            data: JSON.stringify(data),
            success: function (data) {
            },
            error: function (jq, st, error) {
            }
        });
    }
}
let dataController;
$(function () {
    setTimeout(function() {
        dataController = new DataController(dataGlobal);
        ko.applyBindings(dataController);
    }, 500);
});