package org.guce.web.process.vt2.services;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.guce.core.ejb.facade.interfaces.CoreChargerFacadeLocal;
import org.guce.core.ejb.facade.interfaces.CoreStakeHolderFacadeLocal;
import org.guce.core.ejb.facade.interfaces.IdentifiantGeneratorLocal;
import org.guce.core.services.GuceCalendarUtil;
import org.guce.core.services.SearchFilter;
import org.guce.process.vt2.entities.VT2Registration;
import org.guce.process.vt2.repositories.impl.VT2RegistrationRepositoryImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class VT2RegistrationServiceTest {
    protected static List<VT2Registration> list;

    @Mock
    protected VT2RegistrationRepositoryImpl vT2RegistrationRepository;

    @Mock
    protected IdentifiantGeneratorLocal generator;

    @Mock
    protected CoreChargerFacadeLocal chargerFacade;

    @Mock
    protected CoreStakeHolderFacadeLocal stakeHolderFacade;

    @InjectMocks
    protected VT2RegistrationService instance;

    public VT2RegistrationServiceTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        list = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            VT2Registration c = new VT2Registration();
            c.setRecordId(1 + "");
            c.setRecordState("true");
            c.setOfficeCode(nextString(17));
            c.setVtReference(nextString(17));
            c.setVtDate(GuceCalendarUtil.getCalendar().getTime());
            list.add(c);
        }
    }

    @Before
    public void setUp() {
        Mockito.when(vT2RegistrationRepository.count()).thenReturn(list.size());
        Mockito.when(vT2RegistrationRepository.findAll()).thenReturn(list);
        Mockito.when(vT2RegistrationRepository.search(ArgumentMatchers.any(SearchFilter.class), Mockito.anyInt(), Mockito.anyInt(), Mockito.any(String.class), Mockito.any(String.class))).thenReturn(list);
        Mockito.when(vT2RegistrationRepository.searchCount(ArgumentMatchers.any(SearchFilter.class))).thenReturn(list.size());
    }

    @Test
    public void testCount() {
        System.out.println("count");
        int expResult = list.size();
        int result = instance.count();
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void testFindAll_0args() {
        System.out.println("findAll");
        List<VT2Registration> expResult = list;
        List<VT2Registration> result = instance.findAll();
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void testSave_One() {
        System.out.println("save");
        VT2Registration entity = list.get(0);
        entity.setRecordId("55");
        VT2Registration expResult = entity;
        VT2Registration result = instance.save(entity);
        Mockito.verify(vT2RegistrationRepository, Mockito.times(1)).edit(entity);
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void testRemove_One() {
        System.out.println("remove");
        VT2Registration entity = list.get(0);
        instance.remove(entity);
        Mockito.verify(vT2RegistrationRepository, Mockito.times(1)).remove(entity);
    }

    @Test
    public void testRemove_List() {
        System.out.println("remove");
        instance.remove(list);
        Mockito.verify(vT2RegistrationRepository, Mockito.times(10)).remove(ArgumentMatchers.any(VT2Registration.class));
    }

    @Test
    public void testSave_List() {
        System.out.println("save");
        instance.save(list);
        Mockito.verify(vT2RegistrationRepository, Mockito.times(10)).edit(ArgumentMatchers.any(VT2Registration.class));
    }

    @Test
    public void testSearch() {
        System.out.println("search");
        SearchFilter filter = new SearchFilter();
        int start = 0;
        int count = 0;
        String orderField = "";
        String order = "";
        List<VT2Registration> expResult = list;
        List<VT2Registration> result = instance.search(filter, start, count, orderField, order);
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void testSearchCount() {
        System.out.println("searchCount");
        SearchFilter filter = new SearchFilter();
        int expResult = list.size();
        int result = instance.searchCount(filter);
        Assert.assertEquals(expResult, result);
    }

    public static String nextString(int size) {
        byte[] array = new byte[size];
        new Random().nextBytes(array);
        return new String(array, Charset.forName("UTF-8"));
    }
}
