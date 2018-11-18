const API_PREFIX = "http://localhost:4567/"
function browseCategory (categoryName) {
	return fetch(API_PREFIX+"events/"+categoryName).then(response => response.json());
}
function eventDetails (categoryName,eventId) {
	return fetch(API_PREFIX+"events/"+categoryName+'/'+eventId).then(response => response.json())
}
function userDetails (userId) {
	return fetch(API_PREFIX+"users/"+userId).then(response => response.json())
}
function makeEvent(categoryName, eventName, maxMembers, userId) {
	return fetch(API_PREFIX+"events/"+categoryName, {
		method: 'POST',
		body: JSON.stringify({
			eventName: eventName,
			creatorId: userId,
			maxMembers: maxMembers
		})
	}).then(res => res.text())
}
function joinEvent(categoryName, eventId, userId) {
	return fetch(API_PREFIX+"join/"+categoryName +'/'+eventId, {
		method: 'POST',
		body: JSON.stringify({
			userId:userId
		})
	}).then(res => res.text())
}
function verify(categoryName,eventId,file){
	let data = new FormData()
	data.append('image_upload', file)
	return fetch(API_PREFIX+"verify/"+categoryName+"/"+eventId, {
		method: 'POST',
		body: data
	}).then(res => res.text())
}